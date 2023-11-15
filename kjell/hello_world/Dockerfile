FROM python:3.9
COPY app.py .
COPY requirements.txt .
RUN pip3 install -r requirements.txt
ENTRYPOINT ["python","app.py"]
