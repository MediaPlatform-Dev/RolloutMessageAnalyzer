import os
import yaml
from typing import List, Dict
from datetime import datetime
from slack_sdk import WebClient
from slack_sdk.errors import SlackApiError


with open('../resources/application.yml', encoding='UTF-8') as f:
    _cfg = yaml.load(f, Loader=yaml.FullLoader)

CHANNEL_ID = _cfg['service']['slack']['channel']
OLDEST = _cfg['service']['slack']['history']['oldest']
LIMIT = _cfg['service']['slack']['history']['limit']
TOKEN = os.environ['TOKEN']


class MessageEvent:
    def __init__(self, event):
        self.date = datetime.fromtimestamp(float(event['ts']))
        self.image_id = event['attachments'][0]['fields'][-1]['value'].split('/')[-1][:-1]
        self.service = self.image_id.split(':')[0]
        self.status = "Deployment"


def check_status(grouped: Dict) -> Dict:

    for k, v in grouped.items():
        image_id_list = [i.image_id for i in v]
        for idx, val in enumerate(image_id_list):
            if val in image_id_list[:idx]:
                if image_id_list[idx-1].split('-')[-1] < val.split('-')[-1]:
                    v[idx].status = "RE-Deployment"
                elif image_id_list[idx-1].split('-')[-1] > val.split('-')[-1]:
                    v[idx].status = "Rollback"

    return grouped


def grouping_by_service(messages: List) -> Dict:

    result_dict = {}
    for message in messages:
        if not result_dict.get(message.service):
            result_dict[message.service] = [message]
        else:
            result_dict[message.service].append(message)

    return result_dict


def preprocess(messages: List) -> List:
    return sorted([MessageEvent(message) for message in messages if message.get('bot_profile') if message['bot_profile']['name'] == "Argo Notifications"], key=lambda x: x.date)


def get_messages() -> List:

    try:
        return WebClient(token=TOKEN).conversations_history(channel=CHANNEL_ID, oldest=OLDEST, limit=LIMIT)["messages"]
    except SlackApiError as e:
        print("Error creating conversation: {}".format(e))


def main():

    # Slack에서 메세지 가져오기
    messages = get_messages()

    # 메세지에서 원하는 정보 파싱하기
    refined = preprocess(messages)

    # 서비스 별 그룹핑
    grouped = grouping_by_service(refined)
    # for k in grouped:
    #     print(k)
    #     print(len(grouped[k]))
    #     print('-' * 50)

    # 이미지 롤백, 재배포 여부 체크
    checked = check_status(grouped)
    # for k, v in checked.items():
    #     for i in v:
    #         print(i.status)


if __name__ == '__main__':
    main()
