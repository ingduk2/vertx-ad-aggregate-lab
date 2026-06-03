from kafka import KafkaProducer
import json
import random
import time

producer = KafkaProducer(bootstrap_servers='localhost:9092')

placements = [1111, 2222, 3333]
topics = ['ad.request', 'ad.impression', 'ad.click']

count = 0
for i in range(1000):
    placement_id = random.choice(placements)
    topic = random.choice(topics)
    message = {
        "base": {
            "placementId": placement_id,
            "date": "20260601",
            "hour": "10",
            "count": 1
        }
    }
    producer.send(topic, json.dumps(message).encode())
    count += 1

producer.flush()
print(f"전송 완료: {count}건")