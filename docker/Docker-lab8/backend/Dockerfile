FROM python:3.12-slim

WORKDIR /app

RUN pip install flask

COPY . .

EXPOSE 5000

CMD ["python", "app.py"]
