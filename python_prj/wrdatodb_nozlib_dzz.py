# -*- coding: utf-8 -*

# **************************************
# * File Name : wrdatodb.py
# *
# * @author: jerry
# * @version: 1.0
# * @license:
# * ************************************

import os
import sys
import zlib
import sqlite3
import re

# database name
DB_NAME = 'qldz.db'
CREATE_DB_FAKE_SQL = '\'select * from db_test\''
FILE_SEPERATOR = os.sep
# TODO(qingxia): Do not support input src files
# SRC_FILE_PATH = 'qltxt'
SRC_FILE_PATH = 'dzztxt'
SRC_DIC_FILE_PATH = 'dic_new.txt'

# Define database table name
TABLE_PRIMARY_INDEX = 'tablePrimaryIndex'
PRIMARY_INDEX_ID = 'p_id'
PRIMARY_INDEX_NAME = 'p_name'
PRIMARY_INDEX_NAME_SEPERATOR = '、'

TABLE_SECONDARY_INDEX = 'tableSecondaryIndex'
SECONDARY_INDEX_ID = 's_id'
SECONDARY_INDEX_NAME = 's_name'
SEONDARY_INDEX_NAME_SEPERATOR = '～'

TABLE_BUDDHISM_DETAIL = 'tableBuddhismDetail'
BUDDHISM_DETAIL = 'content'
RUBBISH_TEXT = 'This file is decompiled'

# Define the type of dictionary
DIC_TYPE_DINGBAOFU = 1

TABLE_BUDDHISM_DIC = 'tableBuddhismDic'
DIC_ID = 'd_id'
DIC_TYPE = 'd_type'
DIC_KEYWORD = 'd_keyword'
DIC_DESCRIPTION = 'd_description'
DIC_FLAG = '###'

# Define database command
EXECUTE_SQL_COMMAND = 'sqlite3 ' + DB_NAME + ' '
CREATE_TABLE = EXECUTE_SQL_COMMAND + '\"create table if not exists '

# The filename is like this 'index_name'
CREATE_TABLE_PRIMARY_INDEX =\
  CREATE_TABLE + TABLE_PRIMARY_INDEX + ' ('\
  + PRIMARY_INDEX_ID + ' TEXT PRIMARY KEY,'\
  + PRIMARY_INDEX_NAME + ' TEXT' + ');' + '\"'

CREATE_TABLE_SECONDARY_INDEX =\
  CREATE_TABLE + TABLE_SECONDARY_INDEX + ' ('\
  + PRIMARY_INDEX_ID + ' TEXT,'\
  + SECONDARY_INDEX_ID + ' TEXT,'\
  + SECONDARY_INDEX_NAME + ' TEXT'\
  + ');' + '\"'

CREATE_TABLE_BUDDHISM_DETAIL =\
  CREATE_TABLE + TABLE_BUDDHISM_DETAIL + ' ('\
  + SECONDARY_INDEX_ID + ' TEXT,'\
  + SECONDARY_INDEX_NAME + ' TEXT,'\
  + BUDDHISM_DETAIL + ' TEXT'\
  + ');' + '\"'

CREATE_TABLE_DICTIONARY =\
  CREATE_TABLE + TABLE_BUDDHISM_DIC + ' ('\
  + DIC_ID + ' INTEGER PRIMARY KEY,'\
  + DIC_TYPE + ' INTEGER,'\
  + DIC_KEYWORD + ' TEXT,'\
  + DIC_DESCRIPTION + ' TEXT'\
  + ');' + '\"'


# Create db, use a fake sql command to create database
def _createDB():
  print('In _createDB')
  path = os.path.join(os.getcwd(), DB_NAME)
  cmd = 'sqlite3 %s %s' % (path, CREATE_DB_FAKE_SQL)
  os.popen(cmd)


def _init():
  print('In _init')
  # Create database if there is no db
  _createDB()
  global SRC_FILE_PATH
  SRC_FILE_PATH = os.path.join(os.getcwd(), SRC_FILE_PATH)
  global SRC_DIC_FILE_PATH
  SRC_DIC_FILE_PATH = os.path.join(os.getcwd(), SRC_DIC_FILE_PATH)


# Exit with error message and error code
# function name : _errExit
# @param errMsg: String
# @param errCode: number
# @return: None
def _errExit(errMsg, errCode = -1):
  if (errCode != 0):
    print('Err :' + errMsg + ' and Exit.');
    sys.exit(errMsg)


def _insertToPrimaryTable(id, name):
  if (id is None):
    return
  insertCmd = EXECUTE_SQL_COMMAND +\
    '\"INSERT INTO %s VALUES (\'%s\', \'%s\')\"'\
    % (TABLE_PRIMARY_INDEX, id, name)
  # print(insertCmd)
  os.system(insertCmd)


def _createPrimaryTable(srcPath):
  print('In _createPrimaryTable')

  print(CREATE_TABLE_PRIMARY_INDEX)
  os.system(CREATE_TABLE_PRIMARY_INDEX)

  # Insert item to table primary index
  names = os.listdir(srcPath)
  for name in names:
    # print(name)
    exts = name.split(PRIMARY_INDEX_NAME_SEPERATOR)

    if (name.startswith('.')):
      print('We do not handle ' + name)
      continue

    if (len(exts) != 2):
      print(name + ' have a big error.')
      return

    # print(exts[0] + ':' + exts[1])
    _insertToPrimaryTable(exts[0], exts[1])


def _insertToSecondaryTable(pId, sId, sName):
  if (pId is None):
    return
  insertCmd = EXECUTE_SQL_COMMAND +\
    '\"INSERT INTO %s VALUES (\'%s\', \'%s\', \'%s\')\"'\
    % (TABLE_SECONDARY_INDEX, pId, sId, sName)
  # print(insertCmd)
  os.system(insertCmd)


def _insertToBuddhismTable(sId, sName, content):
  if (sId is None or sName is None or content is None):
    return
  insertCmd = EXECUTE_SQL_COMMAND +\
    '\"INSERT INTO %s VALUES (\'%s\', \'%s\', \'%s\')\"'\
    % (TABLE_BUDDHISM_DETAIL, sId, sName, content)
  # print(insertCmd)
  print('Insert content: sId = ' + sId + ' sName = ' + sName)
  os.system(insertCmd)


def _insertToBuddhismTableWithBlob(sId, sName, content):
  if (sId is None or sName is None or content is None):
    return
  # Enable compress
  # contentCompressed = zlib.compress(content, 9)
  dbPath = os.path.join(os.getcwd(), DB_NAME)
  db = sqlite3.connect(dbPath)
  cur = db.cursor()
  # insertCmd = 'INSERT INTO %s VALUES (\'%s\', \'%s\', ?)'\
  #   % (TABLE_BUDDHISM_DETAIL, sId, sName, base64.encodestring(sqlite3.Binary(contentCompressed)))
  db.text_factory = str
  cur.execute("INSERT INTO tableBuddhismDetail VALUES (?, ?, ?)", (sId, sName, content))
  db.commit()
  db.close()


# The first line of sutra is like : '大正藏第 02 册 No. 0140 阿那邠邸化七子经'
# We should return 0140
def _getSutraId(srcPath):
  # print('In _getSutraId')
  if (srcPath is None):
    print('_getSutraId error srcPath is None')

  fileHandle = open(srcPath)
  fileContent = ''
  try:
    fileContent = fileHandle.readlines()
  except Exception, e:
    print e
    print('file:' + srcPath + ' has problem.')
    fileHandle.close()
  else:
    fileHandle.close()

  for line in fileContent:
    retId = re.findall('No.\s*(.*?)\s', line, re.S)

    if (retId != None and len(retId) > 0):
      return str(retId[0]);

  return None



def _insertOneSecondaryIndexItem(primaryIndex, srcPath, dirName):
  # print('In _insertOneSecondaryIndexItem')
  if (primaryIndex is None or srcPath is None or dirName is None):
    _errExit('Parameter error!')
    return

  itemPath = os.path.join(srcPath, dirName)
  # print('itemPath = ' + itemPath)
  if (not os.path.isdir(itemPath)):
    return

  names = os.listdir(itemPath)
  for name in names:
    # print(name)
    if (name.startswith('.')):
      print('We do not handle ' + name)
      continue

    sName = name
    filename = os.path.join(itemPath, name)
    sId = _getSutraId(filename)

    if (sId is None):
      _errExit('%s sutra has error' % filename)

    print('Processing : sId = ' + sId + ' sName = ' + sName)
    fileHandle = open(filename)
    fileContent = ''

    try:
      fileContent = fileHandle.read()
    except Exception, e:
      print e
      print('file:' + filename + ' has problem.')
      fileHandle.close()
    else:
      fileHandle.close()

    # Delete no use text, we have no rubbish data here.
    # position = fileContent.find(RUBBISH_TEXT)
    # if (position != -1):
    #   fileContent = fileContent[0:position].decode('utf-8')
    # else :
    #   fileContent = fileContent.decode('utf-8', 'ignore')

    # Enable compress
    # fileCompressed = zlib.compress(fileContent, zlib.Z_BEST_COMPRESSION)
    # fileContent = fileCompressed

    _insertToBuddhismTableWithBlob(sId, sName, fileContent)
    _insertToSecondaryTable(primaryIndex, sId, sName)
    print('Done : sId = ' + sId + ' sName = ' + sName)


def _createSecondaryTable(srcPath):
  print("In _createSecondaryTable")

  print(CREATE_TABLE_SECONDARY_INDEX)
  os.system(CREATE_TABLE_SECONDARY_INDEX)

  print(CREATE_TABLE_BUDDHISM_DETAIL)
  os.system(CREATE_TABLE_BUDDHISM_DETAIL)

  # Insert item to table secondary index
  names = os.listdir(srcPath)
  for name in names:
    # print(name)

    if (name.startswith('.')):
      print('We do not handle ' + name)
      continue

    exts = name.split(PRIMARY_INDEX_NAME_SEPERATOR)
    if (len(exts) != 2):
      print(name + ' have a big error.')
      return

    _insertOneSecondaryIndexItem(exts[0], srcPath, name)


def _insertToBuddhismDictionaryTable(dicId, keyword, content, dicType = DIC_TYPE_DINGBAOFU):
  if (dicId is None or keyword is None or content is None or dicType is None):
    return

  insertCmd = EXECUTE_SQL_COMMAND +\
    '\"INSERT INTO %s VALUES (\'%s\', \'%s\', \'%s\', \'%s\')\"'\
    % (TABLE_BUDDHISM_DIC, dicId, dicType, keyword, content)
  # print(insertCmd)
  print('Insert content: dicId = ' + str(dicId) + ' dicType = ' + str(dicType)\
    + ' keyword = ' + keyword)
  os.system(insertCmd)


def _createDictionaryTable(srcDicPath):
  print('In _createDictionaryTable')

  if (srcDicPath == None or not os.path.exists(srcDicPath)):
    print('srcDicPath can not be None.')
    return

  print(CREATE_TABLE_DICTIONARY)
  os.system(CREATE_TABLE_DICTIONARY)

  fileHandle = open(srcDicPath)
  fileContent = ''

  try:
    fileContent = fileHandle.readlines()
  except Exception, e:
    print e
    print('file:' + filename + ' has problem.')
    fileHandle.close()
  else:
    fileHandle.close()

  count = 0
  for line in fileContent:
    words = line.split(DIC_FLAG)
    if (len(words) != 2):
      _errExit('Dictionary data error!')

    count += 1
    _insertToBuddhismDictionaryTable(count, words[0], words[1])


def _grepSrcAndWirteToDB(srcPath, srcDicPath = None):
  if (srcPath is None):
    _errExit('src path can not be none')
    return

  # Create TABLE_PRIMARY_INDEX
  _createPrimaryTable(srcPath)

  _createSecondaryTable(srcPath)

  _createDictionaryTable(srcDicPath)


# main function
def _main():
  print('In _main')

  _init()
  _grepSrcAndWirteToDB(SRC_FILE_PATH, SRC_DIC_FILE_PATH)
  print('Create database success')
  sys.exit(0)


_main()