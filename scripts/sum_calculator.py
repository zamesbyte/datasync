#!/usr/bin/env python3
"""
计算从 1 到 1000 的和
"""

def calculate_sum(n):
    """计算从 1 到 n 的和"""
    total = sum(range(1, n + 1))
    return total

if __name__ == "__main__":
    n = 1000
    result = calculate_sum(n)
    print(f"1 + 2 + 3 + ... + {n} = {result}")
    print(f"\n验证公式: n(n+1)/2 = {n * (n + 1) // 2}")
