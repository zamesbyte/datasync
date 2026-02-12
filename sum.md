# 求和计算脚本

## 脚本说明

本脚本用于计算从 1 到 1000 的连续整数之和。

## 脚本内容

脚本位置：`scripts/sum_calculator.py`

```python
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
```

## 执行方法

```bash
python3 scripts/sum_calculator.py
```

## 执行结果

```
1 + 2 + 3 + ... + 1000 = 500500

验证公式: n(n+1)/2 = 500500
```

## 结果说明

- **计算结果**: 500500
- **验证方法**: 使用数学公式 n(n+1)/2 = 1000 × 1001 / 2 = 500500
- **结论**: 从 1 加到 1000 的总和为 **500,500**
