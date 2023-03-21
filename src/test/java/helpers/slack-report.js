const fs = require('fs');
const axios = require('axios');

function createSlackReport(textFilePath, jsonFilePath) {
  const textData = fs.readFileSync(textFilePath, 'utf8');
  const jsonData = JSON.parse(textData);
  const elapsed = (jsonData.elapsedTime / 1000).toFixed(2);
  const threads = jsonData.threads;
  const threadTime = (jsonData.totalTime / 1000).toFixed(2);
  const features = jsonData.featureSummary.length;
  const skipped = jsonData.featuresSkipped;
  const efficiency = jsonData.efficiency.toFixed(2);
  const scenarios = jsonData.scenariosPassed;
  const passed = jsonData.scenariosPassed;
  const failed = jsonData.featuresFailed;

  const baseUrl = 'https://karate-tests.no-zero.net/';
  const date = new Date();
  const day = date.getDate().toString().padStart(2, '0');
  const month = (date.getMonth() + 1).toString().padStart(2, '0');
  const year = date.getFullYear().toString().substr(-2);
  const dateCode = `${day}${month}${year}`;
  const url = baseUrl + 'HWC/' + dateCode + '/overview-features.html';

  const slackText = `elapsed:   ${elapsed} | threads:   ${threads} | thread time: ${threadTime}\nfeatures:     ${features} | skipped:   ${skipped} | efficiency: ${efficiency}\nscenarios:    ${scenarios} | passed:     ${passed} | failed: ${failed}`;

  const slackBlock = 
  {
    "type": "section",
    "text": {
      "type": "mrkdwn",
      "text": slackText
    },
    "accessory": {
      "type": "button",
      "text": {
        "type": "plain_text",
        "text": "View Report"
      },
      "url": url
    }
  }

  const slackJsonData = {
    "blocks": [slackBlock]
  };

  fs.writeFileSync(jsonFilePath, JSON.stringify(slackJsonData, null, 2), 'utf8');

const webhookUrl = process.env.SLACK_WEBHOOK_URL;
  axios.post(webhookUrl, slackJsonData)
    .then(res => console.log('Slack notification sent successfully'))
    .catch(err => console.error('Failed to send Slack notification:', err));
}

const textFilePath = 'target/karate-reports/karate-summary-json.txt';
const jsonFilePath = 'target/slack-block.json';
createSlackReport(textFilePath, jsonFilePath);