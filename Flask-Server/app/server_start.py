from flask import Flask, jsonify, request
from flask_swagger import swagger
import os
import io
import json
from app.chainFunctions import *
from PIL import Image
import base64
from app.face_recognition import *
import re

#  Start the flask server
server = Flask(__name__)

UPLOAD_FOLDER = "/home/ashish/Work/Hackathons/Indo-Singapore Hackathon/Flask-Server/testimages"
server.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER


@server.route("/", methods=['POST', 'GET'])
def spec():
    return jsonify(swagger(server))


@server.route("/image_recog", methods=['POST','GET'])
def images_controller():
    print("server reached")
    # This if is for the faceActivity
    if (len(request.form) is not 0) and (request.form['name'] == "TakeImage"):
        response = {"status": "", "uniqueId": "", "firstName": "", "lastName": "", "contact": "",
                 "newImage":"yes"}
        userName = request.form['userName']
        print(userName)
        spaceIndex = re.search("\s", userName)
        try:
            firstName = userName[0: spaceIndex.span()[0]]
            lastName = userName[spaceIndex.span()[0] + 1:]
        except:
            index = userName.index("@")
            firstName = userName[:index]
            lastName = " "


        userEmail = request.form['email']
        string = request.form['image']

        imgdata = base64.b64decode(string)
        image = Image.open(io.BytesIO(imgdata))
        image.save(UPLOAD_FOLDER + "/Upload.jpg")
        #numFaces, faces = detectFace()
        numFaces = 1
        response = {"status":"", "uniqueId":"", "firstName":"", "lastName":"", "contact":"",
                    "newImage":"yes"}

        if numFaces == 1:
            # Resize to 1000x1000
            response["status"] = "success"
            resize_image()
            #newImage, uniqueId = recognize(faces, firstName, lastName, userEmail)
            newImage, uniqueId = recognize(firstName, lastName, userEmail)
            if newImage is "no":
                response["newImage"] = "no"
            else:
                response["uniqueId"] = uniqueId

            userData = getUserData(uniqueId, userEmail)
            userDict = json.loads(json.loads(userData))
            print(type(userDict))
            response["firstName"] = userDict["firstName"]
            response["lastName"] = userDict["lastName"]
            response["contact"] = userDict["contact"]
            response["uniqueId"] = uniqueId
        else:
            response["status"] = "failed"

        return json.dumps(response)

    # This elif is for the User registeration property
    elif (len(request.form) is not 0) and (request.form['name'] == "AddEvent"):
        eventName = request.form['eventName']
        #eventName = eventName[98:]
        #index = eventName.index(".aspx")
        #eventName = eventName[:index]
        addEvent(request.form['uniqueId'], eventName)
        response = {"status": "success"}
        return json.dumps(response)

    elif (len(request.form) is not 0) and (request.form['name'] == "getUserData"):
        result = getUserData(request.form['uniqueId'], request.form['email'])
        print(result)
        return result

    elif (len(request.form) is not 0) and (request.form['name'] == "updateUserData"):
        result = updateUserData(request.form["uniqueId"], request.form["newContact"],
                                request.form["newFirstName"], request.form["newLastName"])
        print("Inside updateUserInfo " + str(result))
        response = {"status": "success"}
        return json.dumps(response)
