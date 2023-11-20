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

### Metrics context
    - Monitoring on a construction site.
        - Scanning
            - Camera scanning people entering and exiting the area.
            - Camera scanning if people are using Required PPE equipment.
            - Camera scanning people for weapons.
        - Seven Metrics in dashboard
            - To know how many people getting scanned for PPE equipment.
            - To know how many people violating PPE requirements.
            - To know how many people getting scanned for weapons.
            - To know how many weapons was discovered.
            - Gauge to show how many people are in the construction site incase of emergency etc.
            - Two metrics showing the time for doing a scan for PPE and Weapon, 
              this way we can together with the factors, how many persons scanned and how many violations 
              try to oprimize the scanning routine.
