FROM python:3.12-slim
WORKDIR /app
COPY main.py .
RUN pip install --timeout=300 --retries=5 fastapi uvicorn pymongo python-jose[cryptography] requests
EXPOSE 8085
CMD ["python", "main.py"]