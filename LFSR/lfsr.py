# 4, 6, 7, 20, 22, 23
from random import randrange
import time
start_time = time.time()

# random number between 1 and 2^l -1
l = 24
copySeed = seed = randrange(1, 2**l-1); 
# print(seed)

def feedback(seed):
    pos = [1, 3, 4]
    newBit = bool(seed & 1)
    for i in pos:
            newBit ^= bool(seed & (1 << l - i ))
    return newBit

output = ""
count = 0
while True:
    count += 1
    bit = feedback(seed)
    seed = (seed >> 1) | (bit << (l-1))

    if seed == copySeed:
        print("Count = ", count)
        print("Maximum possible period : ", 2**l-1)
        break;
# print(output)
print("Run time: %s seconds" % (time.time() - start_time))
