## Task 1:
### Add secrets and activate actions on GitHub repo.
    - AWS_ACCESS_KEY_ID
    - AWS_SECRET_ACCESS_KEY
    - Settings -> Actions -> General -> Allow all actions and reusable workflows -> save
### Bucket needed to store template.yml, needs to be created manually if AWS env is reset:
    - lambda-deployments-2016

## Task 2:
### Need to create ecr repo manually
    - 2016-repo



## Task 4:
### Ideas metrics 
    - Amount of persons scanned.
    - Amount of persons not violating the requ equipment.

## TODO
     - Maybe add different violations for Head and Hand cover to
      the PPEClassificationResponse.
    - Put in try catch, AmazonRekognitionException.
    - Get rid of Get/set and constructor with lombok annotations.


## Metrics context
* count of people scanned for entering construction area.
* count of people entering construction area without required equipment.
* count of people potentially holding entering with weapon.
* count of people leaving construction area.
* gauge of how many people is in construction area.
* timer for average time spent in construction area. 