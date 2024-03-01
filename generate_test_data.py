import random
import csv
from datetime import datetime, timedelta

def random_call_time(day):
    start = datetime(2020, 1, day, random.randint(0, 23), random.randint(0, 59), 0)
    duration = timedelta(minutes=random.randint(1, 60))
    end = start + duration
    return start.strftime('%d-%m-%Y %H:%M:%S'), end.strftime('%d-%m-%Y %H:%M:%S')

with open('src/test/resources/test_calls.csv', 'w', newline='') as file:
    writer = csv.writer(file)

    for day in range(1, 31):
        for _ in range(33):
            phone_number = f"42077{random.randint(1000000, 9999999)}"
            start_time, end_time = random_call_time(day)
            writer.writerow([phone_number, start_time, end_time])
