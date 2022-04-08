from random import randrange
import sys
import time
start_time = time.time()

N = 256
def ksa(key):
    S = [i for i in range(N)]
    l = len(key)
    j = 0
    for i in range(N):
        j = (j + S[i] + key[i % l]) % N
        S[i], S[j] = S[j], S[i]
    return S

def prga(key):
    S = ksa(key)
    length = len(key)
    i=0
    j=0
    streamkey = [None]*length
    k = 0
    while length > 0 :
        i = (i + 1) % N
        j = (j + S[i]) % N
        S[i], S[j] = S[j], S[i]
        streamkey[k] = S[(S[i] + S[j]) % N]
        k += 1
        length -= 1
    return streamkey

keyLength = 16
key = [randrange(0, 256) for i in range(keyLength)]

count = 0
average = 0
for i in range (10000):
    key = [randrange(0, 256) for i in range(keyLength)]
    keystream = prga(key)
    if keystream[1] == 0:
        average += 1
print("P(Z2)=0:",average/(10000))
print("1/(128)=",1/128)

print("Run time: %s seconds" % (time.time() - start_time))

