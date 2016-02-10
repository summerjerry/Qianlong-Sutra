# -*- coding: utf-8 -*-

import string
import urllib2
import re
import sys
import time
import os


# Define the words here.
DIC_URL_HEADER = 'http://fodian.goodweb.cn/dict_read2.asp?ID=';
PAGE_COUNT = 31589
START_POS = 9873
FILE_HANDLE = None
LOG_ENABLE = False
FILE_NAME = 'dic.txt'


def log(msg):
  if (LOG_ENABLE):
    print(msg)


def _errExit(errMsg, errCode = -1):
  if (errCode != 0):
    print('Err :' + errMsg + ' and Exit.');
    sys.exit(errMsg)


def handlePageData(url):
  global FILE_HANDLE

  if (url == None or len(url) == 0):
    return

  log(url)

  user_agent = 'Mozilla/4.0 (compatible; MSIE 5.5; Windows NT)'
  headers = { 'User-Agent' : user_agent }
  req = urllib2.Request(url, headers = headers)
  response = urllib2.urlopen(req)
  pageData = response.read().decode('gbk')
  # log(pageData)
  items = re.findall('bgcolor="#F8FCFE">(.*?)/B>', pageData, re.S)
  content = ''
  if (len(items) != 1):
    log('id ' + url + ' can not be fetch \n')
    return

  content = items[0]
  # log(content)

  # 用非 贪婪模式 匹配 任意<>标签
  endCharToNoneRex = re.compile("<.*?>")
  bgnCharToNoneRex = re.compile("(\t|\r|\n)")
  # Get the title
  titles = re.findall('<span class=style1>(.*?)</span>', content, re.S)
  title = titles[0][1:len(titles[0]) - 1].encode('utf-8')
  title = bgnCharToNoneRex.sub('', title)
  log('title = ' + title)

  # Get the description
  descriptions = re.findall('<BR><BR>(.*?)]<', content, re.S)
  description = descriptions[0].encode('utf-8') + ']'
  description = endCharToNoneRex.sub('', description)
  description = bgnCharToNoneRex.sub('', description)
  log('description = ' + description)
  FILE_HANDLE.write('%s###%s\n' % (title, description))


def exeSysCmd(cmd):
  if (cmd):
    if (os.system(cmd) != 0):
      print("echo cmd %s error" % cmd)


def rmDir(path):
  if (path == None):
    return

  log('rm dir :' + path)
  cmd = 'rm -rf ' + path
  exeSysCmd(cmd)


def handleData():
  global FILE_HANDLE
  global START_POS

  log('handle data')
  if (os.path.isfile(FILE_NAME) and START_POS == 1):
    rmDir(FILE_NAME)

  FILE_HANDLE = open(FILE_NAME, 'a')

  for i in range(START_POS, PAGE_COUNT + 1):
    handlePageData(DIC_URL_HEADER + str(i))
    time.sleep(0.01)

  FILE_HANDLE.close()


def main():
  handleData()
  pass;


main()
