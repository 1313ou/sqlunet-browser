#!/usr/bin/python3

import sqlite3
import sys
import os

sql_schema="""SELECT * FROM sqlite_schema;"""
sql_tables_schema="""SELECT * FROM sqlite_schema WHERE type='table';"""
sql_indexes_schema="""SELECT * FROM sqlite_schema WHERE type='index';"""

sql_sizes="""SELECT name, SUM(pgsize) as size
FROM sqlite_schema AS s
LEFT JOIN dbstat AS c USING (name)
GROUP BY name;"""

def hr(x):
	if x == None:
		return "NA"
	f="%.0f %s"
	for unit in ['B','KB','MB','GB']:
		if x > -1024.0 and x < 1024.0:
			return f % (round(x), unit)
		x /= 1024.0
	return f % (round(x), 'TB')

def out(rows):
	for row in rows:
		print("%-48s	%10s	%10s" % (row[0], row[1], hr(row[1])))
	print("\n")

def query(sql, consume):
	cursor = connection.cursor()
	cursor.execute(sql)
	rows = cursor.fetchall()
	consume(rows)
	cursor.close()

where=sys.argv[1]
db=where+sys.argv[2]
connection = sqlite3.connect(db)

if __name__ == "__main__":

	print(db)
	print("----------------------------------------------------------------------------------")
	query(sql_sizes, out)
