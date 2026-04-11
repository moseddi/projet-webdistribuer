from fastapi import FastAPI, HTTPException, Depends, Header
from pymongo import MongoClient
from pydantic import BaseModel
from datetime import datetime
from bson import ObjectId
import uvicorn
import threading
import requests
import time

app = FastAPI(title="Review Service")

# MongoDB connection
client = MongoClient("mongodb://localhost:27017/")
db = client["review_db"]
reviews_collection = db["reviews"]

# Config
EUREKA_URL = "http://localhost:8761/eureka/apps/review-service"
SERVICE_HOST = "localhost"
SERVICE_PORT = 8085

# Models
class ReviewCreate(BaseModel):
    productId: int
    rating: float
    comment: str

# Eureka registration
def register_with_eureka():
    while True:
        try:
            response = requests.post(
                EUREKA_URL,
                json={
                    "instance": {
                        "instanceId": f"localhost:{SERVICE_PORT}",
                        "hostName": "localhost",
                        "app": "REVIEW-SERVICE",
                        "ipAddr": "127.0.0.1",
                        "status": "UP",
                        "port": {"$": SERVICE_PORT, "@enabled": "true"},
                        "healthCheckUrl": f"http://localhost:{SERVICE_PORT}/actuator/health",
                        "statusPageUrl": f"http://localhost:{SERVICE_PORT}/actuator/health",
                        "homePageUrl": f"http://localhost:{SERVICE_PORT}/",
                        "vipAddress": "review-service",
                        "secureVipAddress": "review-service",
                        "dataCenterInfo": {
                            "@class": "com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo",
                            "name": "MyOwn"
                        },
                        "leaseInfo": {
                            "renewalIntervalInSecs": 30,
                            "durationInSecs": 90
                        }
                    }
                },
                headers={"Content-Type": "application/json"}
            )
            print(f"✅ Registered with Eureka: {response.status_code}")
        except Exception as e:
            print(f"❌ Eureka registration failed: {e}")
        time.sleep(30)

# JWT validation
def get_current_user(authorization: str = Header(...)):
    try:
        token = authorization.replace("Bearer ", "")
        from jose import jwt
        payload = jwt.get_unverified_claims(token)
        username = payload.get("preferred_username")
        roles = payload.get("realm_access", {}).get("roles", [])
        if not username:
            raise HTTPException(status_code=401, detail="Invalid token")
        return {"username": username, "roles": roles}
    except Exception as e:
        raise HTTPException(status_code=401, detail="Invalid token")

# Routes
@app.get("/actuator/health")
def health():
    return {"status": "UP"}

# GET all reviews
@app.get("/api/v1/reviews")
def get_all_reviews(user: dict = Depends(get_current_user)):
    reviews = []
    for r in reviews_collection.find():
        r["id"] = str(r["_id"])
        r.pop("_id")
        reviews.append(r)
    return reviews

# CREATE review
@app.post("/api/v1/reviews")
def create_review(review: ReviewCreate, user: dict = Depends(get_current_user)):
    # Check if user already reviewed this product
    existing = reviews_collection.find_one({
        "productId": review.productId,
        "username": user["username"]
    })
    if existing:
        raise HTTPException(status_code=400, detail="You already reviewed this product")

    review_doc = {
        "productId": review.productId,
        "username": user["username"],
        "rating": review.rating,
        "comment": review.comment,
        "createdAt": datetime.now().isoformat()
    }
    result = reviews_collection.insert_one(review_doc)
    review_doc["id"] = str(result.inserted_id)
    review_doc.pop("_id")
    return review_doc

# GET reviews by product
@app.get("/api/v1/reviews/product/{product_id}")
def get_reviews_by_product(product_id: int, user: dict = Depends(get_current_user)):
    reviews = []
    for r in reviews_collection.find({"productId": product_id}):
        r["id"] = str(r["_id"])
        r.pop("_id")
        reviews.append(r)
    return reviews

# GET average rating for product
@app.get("/api/v1/reviews/product/{product_id}/average")
def get_average_rating(product_id: int, user: dict = Depends(get_current_user)):
    reviews = list(reviews_collection.find({"productId": product_id}))
    if not reviews:
        return {"productId": product_id, "averageRating": 0, "totalReviews": 0}
    avg = sum(r["rating"] for r in reviews) / len(reviews)
    return {
        "productId": product_id,
        "averageRating": round(avg, 2),
        "totalReviews": len(reviews)
    }

# GET reviews by user
@app.get("/api/v1/reviews/user/{username}")
def get_reviews_by_user(username: str, user: dict = Depends(get_current_user)):
    reviews = []
    for r in reviews_collection.find({"username": username}):
        r["id"] = str(r["_id"])
        r.pop("_id")
        reviews.append(r)
    return reviews

# UPDATE review (only owner)
@app.put("/api/v1/reviews/{review_id}")
def update_review(review_id: str, review: ReviewCreate, user: dict = Depends(get_current_user)):
    result = reviews_collection.update_one(
        {"_id": ObjectId(review_id), "username": user["username"]},
        {"$set": {
            "rating": review.rating,
            "comment": review.comment,
            "updatedAt": datetime.now().isoformat()
        }}
    )
    if result.matched_count == 0:
        raise HTTPException(status_code=404, detail="Review not found or not yours")
    return {"message": "Review updated ✅"}

# DELETE review (ADMIN or owner)
@app.delete("/api/v1/reviews/{review_id}")
def delete_review(review_id: str, user: dict = Depends(get_current_user)):
    is_admin = "ADMIN" in user["roles"]

    if is_admin:
        # Admin can delete any review
        result = reviews_collection.delete_one({"_id": ObjectId(review_id)})
    else:
        # Client can only delete their own review
        result = reviews_collection.delete_one({
            "_id": ObjectId(review_id),
            "username": user["username"]
        })

    if result.deleted_count == 0:
        raise HTTPException(status_code=404, detail="Review not found or not authorized")
    return {"message": "Review deleted ✅"}

@app.on_event("startup")
async def startup_event():
    threading.Thread(target=register_with_eureka, daemon=True).start()
    print("🚀 Review Service started on port 8085")

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8085)