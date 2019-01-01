import csv
import os
import re


class Word():
    def __init__(self, row):
        self.spanish_words = row[0].split(' / ')
        self.japanese_words = row[1].split(' / ')
        self.spanish_examples = row[2].split(' / ')
        self.japanese_examples = row[3].split(' / ')

# 単語データの例文にかっこがあるかチェック
# かっこがあればFalse, なければTrue
def check_example_kakko(word):
    reg = re.compile(r'\(.*\)')

    for spanish_example in word.spanish_examples:
        if len(spanish_example) == 0:
            continue

        res = reg.findall(spanish_example)
        if len(res) == 0:
            print('スペイン語の例文にかっこがありません。')
            return True

    return False

def check_example_count(word):
    if len(word.spanish_examples) != len(word.japanese_examples):
        print('スペイン語と日本語の例文の数が合っていません。')
        return True

    return False

def main():
    for file_name in os.listdir('./csv'):
        file_path = './csv/' + file_name

        print('Check %s ...' % file_path)

        csv_file = open(file_path, 'r', encoding='UTF-8')
        reader = csv.reader(csv_file)

        next(reader)

        count = 2
        for row in reader:
            word = Word(row)

            res = False

            res = check_example_kakko(word)
            res = check_example_count(word) or res

            if res:
                print('Invalid Data! (Row: %d)' % count)
            count += 1

    print('Finish checking csv')

if __name__ == '__main__':
    main()
