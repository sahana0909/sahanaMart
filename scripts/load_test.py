"""
SahanaMart Automated Load Testing Script
Simulates 10 concurrent users generating continuous traffic for 60 seconds.
Conforms to Anna University R2025 Capstone Week 7 Requirement:
"Run a basic load test with Apache JMeter or ab: minimum 10 concurrent users for 60 seconds."
"""

import time
import urllib.request
import threading
import sys

BASE_URL = sys.argv[1] if len(sys.argv) > 1 else "http://localhost:8080/sahanamart"
CONCURRENT_USERS = 10
DURATION_SECONDS = 60

stats = {
    "requests": 0,
    "success": 0,
    "failed": 0,
    "latencies": []
}
lock = threading.Lock()

ENDPOINTS = [
    "/api/v1/health",
    "/products",
    "/api/v1/products",
    "/login",
    "/register"
]

def user_worker(worker_id, stop_time):
    idx = 0
    while time.time() < stop_time:
        path = ENDPOINTS[idx % len(ENDPOINTS)]
        idx += 1
        url = f"{BASE_URL}{path}"
        start = time.time()
        try:
            req = urllib.request.Request(url, headers={"User-Agent": f"LoadTester-Worker-{worker_id}"})
            with urllib.request.urlopen(req, timeout=5) as response:
                status = response.getcode()
                latency = time.time() - start
                with lock:
                    stats["requests"] += 1
                    if 200 <= status < 400:
                        stats["success"] += 1
                    else:
                        stats["failed"] += 1
                    stats["latencies"].append(latency)
        except Exception as e:
            with lock:
                stats["requests"] += 1
                stats["failed"] += 1
        time.sleep(0.05) # simulate realistic user pause

def run_load_test():
    print(f"==================================================")
    print(f"Starting SahanaMart Load Test")
    print(f"Target URL: {BASE_URL}")
    print(f"Concurrency: {CONCURRENT_USERS} workers | Duration: {DURATION_SECONDS} seconds")
    print(f"==================================================")

    start_time = time.time()
    stop_time = start_time + DURATION_SECONDS

    threads = []
    for i in range(CONCURRENT_USERS):
        t = threading.Thread(target=user_worker, args=(i, stop_time))
        threads.append(t)
        t.start()

    for t in threads:
        t.join()

    total_time = time.time() - start_time
    avg_latency = (sum(stats["latencies"]) / len(stats["latencies"])) * 1000 if stats["latencies"] else 0
    throughput = stats["requests"] / total_time if total_time > 0 else 0

    print(f"\n================ Load Test Results ================")
    print(f"Total Duration:     {total_time:.2f} s")
    print(f"Total Requests:     {stats['requests']}")
    print(f"Successful (2xx):   {stats['success']}")
    print(f"Failed / Errors:    {stats['failed']}")
    print(f"Throughput:         {throughput:.2f} req/s")
    print(f"Average Latency:    {avg_latency:.2f} ms")
    print(f"Success Rate:       {(stats['success'] / stats['requests'] * 100) if stats['requests'] else 0:.1f}%")
    print(f"==================================================")

if __name__ == "__main__":
    run_load_test()
