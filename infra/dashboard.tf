resource "aws_cloudwatch_dashboard" "main" {
  dashboard_name = "Dashboard-${var.prefix}"
  dashboard_body = <<DASHBOARD
{
  "widgets": [

    {
      "type": "alarm",
      "x": 0,
      "y": 0,
      "width": 6,
      "height": 3,
      "properties": {
        "alarms": ["2016-weapon-detected"
        ],
        "region": "eu-west-1",
        "title": "Weapon alarm"
      }
    },
    {
      "type": "metric",
      "x": 0,
      "y": 0,
      "width": 12,
      "height": 6,
      "properties": {
        "metrics": [
          [
            "${var.prefix}",
            "ppe_scan_count.count"
          ]
        ],
        "period": 300,
        "stat": "Maximum",
        "region": "eu-west-1",
        "title": "Total count of persons scanned for PPE violation"
      }
    },
    {
      "type": "metric",
      "x": 13,
      "y": 7,
      "width": 12,
      "height": 6,
      "properties": {
        "metrics": [
          [
            "${var.prefix}",
            "ppe_scan_violation_count.count"
          ]
        ],
        "period": 300,
        "stat": "Maximum",
        "region": "eu-west-1",
        "title": "Persons without required PPE equipment"
      }
    },
    {
      "type": "metric",
      "x": 13,
      "y": 21,
      "width": 12,
      "height": 6,
      "properties": {
        "metrics": [
          [
            "${var.prefix}",
            "weapon_scan_violation_count.count"
          ]
        ],
        "period": 300,
        "stat": "Maximum",
        "region": "eu-west-1",
        "title": "Weapons detected"
      }
    },
    {
      "type": "metric",
      "x": 0,
      "y": 14,
      "width": 12,
      "height": 6,
      "properties": {
        "metrics": [
          [
            "${var.prefix}",
            "weapon_scan_count.count"
          ]
        ],
        "period": 300,
        "stat": "Maximum",
        "region": "eu-west-1",
        "title": "Total count of persons scanned for weapons violation"
      }
    },
    {
      "type": "metric",
      "x": 0,
      "y": 28,
      "width": 12,
      "height": 6,
      "properties": {
        "metrics": [
          [
            "${var.prefix}",
            "persons_in_area_count.value"
          ]
        ],
        "period": 300,
        "stat": "Maximum",
        "region": "eu-west-1",
        "view": "gauge",
        "title": "Amount of persons in construction area",
        "yAxis": {
          "left": {
            "min": 0,
            "max": 100
          }
        }
      }
    },
    {
      "type": "metric",
      "stacked": false,
      "width": 12,
      "height": 6,
      "properties": {
        "metrics": [
          [
            "${var.prefix}",
            "scan_time.avg",
            "exception",
            "none",
            "method",
            "scanForPPE",
            "class",
            "com.example.s3rekognition.controller.RekognitionController"
          ]
        ],
        "view": "timeSeries",
        "period": 300,
        "region": "eu-west-1",
        "title": "Time for processing a PPE scan"
      }
    },
    {
      "type": "metric",
      "stacked": false,
      "width": 12,
      "height": 6,
      "properties": {
        "metrics": [
          [
            "${var.prefix}",
            "scan_time.avg",
            "exception",
            "none",
            "method",
            "scanForWeapon",
            "class",
            "com.example.s3rekognition.controller.RekognitionController"
          ]
        ],
        "view": "timeSeries",
        "period": 300,
        "region": "eu-west-1",
        "title": "Time for processing a Weapon scan"
      }
    }
  ]
}
DASHBOARD
}

module "alarm" {
  source = "./alarm_module"
  alarm_email = var.alarm_email
  prefix = var.prefix
}