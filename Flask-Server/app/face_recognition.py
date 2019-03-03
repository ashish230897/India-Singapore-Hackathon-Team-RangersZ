import os
import face_recognition
import cv2
import hashlib
import dlib
from app.chainFunctions import *

path = '/home/ashish/Work/Hackathons/Indo-Singapore Hackathon/Flask-Server'
desired_size = 1000
matchedPicId = "null"


def resize_image():
    img = cv2.imread(path + "/testimages/Upload.jpg")
    rows, cols, _ = img.shape
    img = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    M = cv2.getRotationMatrix2D((cols / 2, rows / 2), 90, 1)
    dst = cv2.warpAffine(img, M, (cols, rows))
    cv2.imwrite(path + "/testimages/Upload.jpg", dst)

    im = dst
    #im = cv2.imread(path + "/testimages/Upload.jpg")
    old_size = im.shape[:2] # old_size is in (height, width) format
    ratio = float(desired_size)/max(old_size)
    new_size = tuple([int(x*ratio) for x in old_size])

    # new_size should be in (width, height) format
    im = cv2.resize(im, (new_size[1], new_size[0]))
    delta_w = desired_size - new_size[1]
    delta_h = desired_size - new_size[0]
    top, bottom = delta_h//2, delta_h-(delta_h//2)
    left, right = delta_w//2, delta_w-(delta_w//2)
    color = [0, 0, 0]
    new_im = cv2.copyMakeBorder(im, top, bottom, left, right, cv2.BORDER_CONSTANT,
        value=color)

    cv2.imwrite(path + "/testimages/Upload.jpg", new_im)
    print("Image resized")


def detectFace():
    img = cv2.imread(path + "/testimages/Upload.jpg")
    rows, cols, _ = img.shape
    img = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    M = cv2.getRotationMatrix2D((cols / 2, rows / 2), 90, 1)
    dst = cv2.warpAffine(img, M, (cols, rows))
    cv2.imwrite(path + "/testimages/Upload.jpg", dst)

    detector = dlib.get_frontal_face_detector()
    faces = detector(dst, 1)
    print("Face detected")
    return len(faces), faces


def recognize(firstName, lastName, userEmail):
    # make a list of all the available images
    images = os.listdir(path + '/images')

    # load your image
    image_to_be_matched = face_recognition.load_image_file(path + '/testimages/Upload.jpg')

    # encoded the loaded image into a feature vector
    image_to_be_matched_encoded = face_recognition.face_encodings(
        image_to_be_matched)[0]

    count = 0
    # iterate over each image
    for image in images:
        # load the image
        current_image = face_recognition.load_image_file(path + "/images/" + image)
        # encode the loaded image into a feature vector
        current_image_encoded = face_recognition.face_encodings(current_image)[0]
        # match your image with the image and check if it matches
        result = face_recognition.compare_faces(
            [image_to_be_matched_encoded], current_image_encoded)

        # check if it was a match
        if result[0] == True:
            print("Matched: " + image)
            matchedPicId = str(image[:-4])
            count += 1
            break
    print("count : " + str(count))
    if count is 0:
        list_image = list()

        left = 200
        #for i, d in enumerate(faces):
        #    left = d.left()
        list_image_ = list(image_to_be_matched[left:left + 300][left:left + 100])

        for array in list_image_:
            list_image += list(array)
        list_image = str(list_image)
        uniqueId = hashlib.md5(list_image.encode())
        uniqueId = uniqueId.hexdigest()
        cv2.imwrite(path + "/images/" + uniqueId + ".jpg", image_to_be_matched)

        createParticipant(uniqueId)
        addDetails(uniqueId, firstName, lastName, userEmail)
        addDigitalIdentity(uniqueId, userEmail)
        return "yes", uniqueId
    else:
        # Chain function to update the email id in the emails list
        addDigitalIdentity(matchedPicId, userEmail)
        print(matchedPicId)
        return "no", matchedPicId
