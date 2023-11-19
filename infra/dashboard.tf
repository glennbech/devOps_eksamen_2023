resource "aws_cloudwatch_dashboard" "main" {
  dashboard_name = "Dashboard-${var.prefix}"
  dashboard_body = <<DASHBOARD
{
  "widgets": [

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
        "title": "Count of persons scanned"
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
            "ppe_scan_violation_count.count"
          ]
        ],
        "period": 300,
        "stat": "Maximum",
        "region": "eu-west-1",
        "title": "Count of persons without required PPE equipment"
      }
    }

  ]
}
DASHBOARD
}