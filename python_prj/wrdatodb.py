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
import base64

# database name
DB_NAME = 'qldzlib.db'
CREATE_DB_FAKE_SQL = '\'select * from db_test\''
FILE_SEPERATOR = os.sep
# TODO(qingxia): Do not support input src files
SRC_FILE_PATH = 'qltxt'

# Define database table name
TABLE_PRIMARY_INDEX = 'tablePrimaryIndex'
PRIMARY_INDEX_ID = 'p_id'
PRIMARY_INDEX_NAME = 'p_name'
PRIMARY_INDEX_NAME_SEPERATOR = '_'

TABLE_SECONDARY_INDEX = 'tableSecondaryIndex'
SECONDARY_INDEX_ID = 's_id'
SECONDARY_INDEX_NAME = 's_name'
SEONDARY_INDEX_NAME_SEPERATOR = '～'

TABLE_BUDDHISM_DETAIL = 'tableBuddhismDetail'
BUDDHISM_DETAIL = 'content'
RUBBBISH_TEXT = 'This file is decompiled'

# Define database command
EXECUTE_SQL_COMMAND = 'sqlite3 ' + DB_NAME + ' '
CREATE_TABLE = EXECUTE_SQL_COMMAND + '\"create table if not exists '

# The filename is like this 'index_name'
CREATE_TABLE_PRIMARY_INDEX =\
  CREATE_TABLE + TABLE_PRIMARY_INDEX + ' ('\
  + PRIMARY_INDEX_ID + ' INTEGER PRIMARY KEY,'\
  + PRIMARY_INDEX_NAME + ' TEXT' + ');' + '\"'

CREATE_TABLE_SECONDARY_INDEX =\
  CREATE_TABLE + TABLE_SECONDARY_INDEX + ' ('\
  + PRIMARY_INDEX_ID + ' INTEGER,'\
  + SECONDARY_INDEX_ID + ' TEXT,'\
  + SECONDARY_INDEX_NAME + ' TEXT'\
  + ');' + '\"'

CREATE_TABLE_TABLE_BUDDHISM_DETAIL =\
  CREATE_TABLE + TABLE_BUDDHISM_DETAIL + ' ('\
  + SECONDARY_INDEX_ID + ' TEXT,'\
  + SECONDARY_INDEX_NAME + ' TEXT,'\
  + BUDDHISM_DETAIL + ' BLOB'\
  + ');' + '\"'

def _getCurrentPath():
  output = os.popen('pwd')
  return output.read().strip()


# Create db, use a fake sql command to create database
def _createDB():
  print('In _createDB')
  path = _getCurrentPath() + FILE_SEPERATOR + DB_NAME
  cmd = 'sqlite3 %s %s' % (path, CREATE_DB_FAKE_SQL)
  os.popen(cmd)


def _init():
  print('In _init')
  # Create database if there is no db
  _createDB()
  global SRC_FILE_PATH
  SRC_FILE_PATH = _getCurrentPath() + FILE_SEPERATOR + SRC_FILE_PATH


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


def _insertToBuddhismTableWithBlob(sId, sName, content):
  if (sId is None or sName is None or content is None):
    return
  # Enable compress
  contentCompressed = zlib.compress(content, 9)
  dbPath = os.path.join(_getCurrentPath(), DB_NAME)
  db = sqlite3.connect(dbPath)
  cur = db.cursor()
  # insertCmd = 'INSERT INTO %s VALUES (\'%s\', \'%s\', ?)'\
  #   % (TABLE_BUDDHISM_DETAIL, sId, sName, base64.encodestring(sqlite3.Binary(contentCompressed)))
  db.text_factory = str
  cur.execute("INSERT INTO tableBuddhismDetail VALUES (?, ?, ?)", (sId, sName, contentCompressed))
  db.commit()
  db.close()


def _insertToBuddhismTable(sId, sName, content):
  if (sId is None or sName is None or content is None):
    return
  insertCmd = EXECUTE_SQL_COMMAND +\
    '\"INSERT INTO %s VALUES (\'%s\', \'%s\', \'%s\')\"'\
    % (TABLE_BUDDHISM_DETAIL, sId, sName, content)
  # print(insertCmd)
  os.system(insertCmd)


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

    position = name.find(SEONDARY_INDEX_NAME_SEPERATOR)
    if (position == -1):
      continue

    # NOTE(qingxia): One chinese word take 3 position.
    sId = name[3:position - 3]
    # NOTE(qingxia): We should remove the .txt
    sName = name[position + 3:-4]
    sName = sName.replace(SEONDARY_INDEX_NAME_SEPERATOR, '-')
    print('sId = ' + sId + ' sName = ' + sName)
    filename = os.path.join(itemPath, name)
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

    # Delete no use text
    position = fileContent.find(RUBBBISH_TEXT)
    if (position != -1):
      fileContent = fileContent[0:position].decode('gb18030', 'ignore')\
        .encode('utf-8')
    else :
      fileContent = fileContent.decode('gb18030', 'ignore')\
        .encode('utf-8')

    _insertToBuddhismTableWithBlob(sId, sName, fileContent)
    _insertToSecondaryTable(primaryIndex, sId, sName)


def _createSecondaryTable(srcPath):
  print("In _createSecondaryTable")

  print(CREATE_TABLE_SECONDARY_INDEX)
  os.system(CREATE_TABLE_SECONDARY_INDEX)

  print(CREATE_TABLE_TABLE_BUDDHISM_DETAIL)
  os.system(CREATE_TABLE_TABLE_BUDDHISM_DETAIL)

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


def _grepSrcAndWirteToDB(srcPath):
  if (srcPath is None):
    _errExit('src path can not be none')
    return

  # Create TABLE_PRIMARY_INDEX
  _createPrimaryTable(srcPath)

  _createSecondaryTable(srcPath)


# main function
def _main():
  print('In _main')

  _init()
  _grepSrcAndWirteToDB(SRC_FILE_PATH)
  print('Create database success')
  sys.exit(0)


_main()