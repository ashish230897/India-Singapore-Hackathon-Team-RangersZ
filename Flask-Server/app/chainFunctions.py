import requests

# This is the IP address of the rest server, the blockchain business network can be interacted with
# using this server.
url = "http://192.168.0.40:3000/api/"
namespace = "org.example.identity."


def createParticipant(uniqueId):
    payload = {"$class": namespace + "User", "userID": uniqueId}
    response = requests.post(url + "User", data=payload)
    print(response)


def addDetails(uniqueId, firstName, lastName, userEmail):
    payload = {"$class": namespace + "UserDetails", "userID": uniqueId,
               "contact":"000", "firstName":firstName, "lastName":lastName,
               "digitalIdentities":[], "events":[], "owner":namespace + "User#" + uniqueId}
    response = requests.post(url + "UserDetails", data=payload)
    print(response)


def getUserData(uniqueId, email):
    payload = {"$class": namespace + "getUserData", "userID": uniqueId,
               "email": email}
    response = requests.post(url + "getUserData", data=payload)
    return response.text

def addDigitalIdentity(uniqueId, email):
    payload = {"$class": namespace + "AddDigitalIdentity", "sd": namespace + "UserDetails#" + uniqueId,
               "newDigitalIdentity": email}
    response = requests.post(url + "AddDigitalIdentity", data=payload)
    print(response)


def addEvent(uniqueId, eventName):
    payload = {"$class": namespace + "AddEvent", "sd": namespace + "UserDetails#" + uniqueId,
               "newEvent": eventName}
    response = requests.post(url + "AddEvent", data=payload)
    print(str(response) + " in addEvent")


def checkUser(uniqueId):
    payload = {"$class": namespace + "checkUser", "userID": uniqueId}
    response = requests.post(url + "checkUser", data=payload)
    print(response.text)


def updateUserData(uniqueId, newContact, newFirstName, newLastName):
    payload = {"$class": namespace + "UpdateUserData", "sd": namespace + "UserDetails#" + uniqueId,
               "newContact": newContact, "newFirstName": newFirstName, "newLastName": newLastName}
    response = requests.post(url + "UpdateUserData", data=payload)
    return response
