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

sql_total_size="""SELECT SUM(size) FROM (
SELECT name, SUM(pgsize) as size
FROM sqlite_schema AS s
LEFT JOIN dbstat AS c USING (name)
GROUP BY name);"""

sql_total_table_size="""SELECT SUM(size) FROM (
SELECT name, SUM(pgsize) as size
FROM sqlite_schema AS s
LEFT JOIN dbstat AS c USING (name)
WHERE type='table'
GROUP BY name);"""

sql_total_nonfts_table_size="""SELECT SUM(size) FROM (
SELECT name, SUM(pgsize) as size
FROM sqlite_schema AS s
LEFT JOIN dbstat AS c USING (name)
WHERE type='table' AND name NOT LIKE '%fts4%'
GROUP BY name);"""

sql_total_fts_table_size="""SELECT SUM(size) FROM (
SELECT name, SUM(pgsize) as size
FROM sqlite_schema AS s
LEFT JOIN dbstat AS c USING (name)
WHERE type='table' AND name LIKE '%fts4%'
GROUP BY name);"""

sql_total_fts_extra_table_size="""SELECT SUM(size) FROM (
SELECT name, SUM(pgsize) as size
FROM sqlite_schema AS s
LEFT JOIN dbstat AS c USING (name)
WHERE type='table' AND name LIKE '%%fts4%%' AND %s 
GROUP BY name);"""

sql_total_index_size="""SELECT SUM(size) FROM (
SELECT name, SUM(pgsize) as size
FROM sqlite_schema AS s
LEFT JOIN dbstat AS c USING (name)
WHERE type='index'
GROUP BY name);"""

sql_total_nonfts_index_size="""SELECT SUM(size) FROM (
SELECT name, SUM(pgsize) as size
FROM sqlite_schema AS s
LEFT JOIN dbstat AS c USING (name)
WHERE type='index'
GROUP BY name);"""

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

def out1(rows):
	for row in rows:
		print("%-48s	%10s	%10s" % ('',row[0], hr(row[0])))
	print("\n")

collectedRaw=[]
collectedHR=[]

def outRes(rows,rawf,hrf):
	for row in rows:
		collectedRaw.append(rawf % row[0])
		collectedHR.append(hrf % hr(row[0]))

def filesizeRes(f,rawf,hrf):
	b = os.path.getsize(f)
	collectedRaw.append(rawf % (b,os.path.basename(f)))
	collectedHR.append(rawf % (hr(b),os.path.basename(f)))

def query(sql, consume):
	cursor = connection.cursor()
	cursor.execute(sql)
	rows = cursor.fetchall()
	consume(rows)
	cursor.close()

db=sys.argv[1]
dbf=sys.argv[2]
dbfz=sys.argv[3]
extra=sys.argv[4:]
connection = sqlite3.connect(db)

print("table sizes")
query(sql_sizes, out)

#print("total size")
query(sql_total_size, lambda r: outRes(r,
	"<integer name='size_db_working_total' tools:keep='@string/size_db_working_total'>%s</integer>",
	"<string name='hr_size_db_working_total' tools:keep='@string/hr_size_db_working_total'>%s</string>"))

#print("total table size")
#query(sql_total_table_size, lambda r: outRes(r,
#	"<integer name='size_tables' tools:keep='@string/size_tables'>%s</integer>",
#	"<string name='hr_size_tables' tools:keep='@string/hr_size_tables'>%s</string>"))

#print("total_core table size")
query(sql_total_nonfts_table_size, lambda r: outRes(r,
	"<integer name='size_core' tools:keep='@string/size_core'>%s</integer>",
	"<string name='hr_size_core' tools:keep='@string/hr_size_core'>%s</string>"))

#print("total indexes size")
query(sql_total_index_size, lambda r: outRes(r,
	"<integer name='size_indexes' tools:keep='@string/size_indexes'>%s</integer>",
	"<string name='hr_size_indexes' tools:keep='@string/hr_size_indexes'>%s</string>"))

#print("total fts table size")
query(sql_total_fts_table_size, lambda r: outRes(r,
	"<integer name='size_searchtext' tools:keep='@string/size_searchtext'>%s</integer>",
	"<string name='hr_size_searchtext' tools:keep='@string/hr_size_searchtext'>%s</string>"))

for ts in extra:
	extra = "name LIKE '%%%s%%'" % ts
	sql = sql_total_fts_extra_table_size % extra
	print(extra)
	print(sql)
	query(sql_total_fts_extra_table_size % extra, lambda r: outRes(r,
		"<integer name='size_%s_searchtext' tools:keep='@string/size_%s_searchtext'>%%s</integer>" % (ts,ts),
		"<string name='hr_size_%s_searchtext' tools:keep='@string/hr_size_%s_searchtext'>%%s</string>" % (ts,ts),))

filesizeRes(dbf,
	"<integer name='size_sqlunet_db' tools:keep='@string/size_sqlunet_db'>%s</integer> <!-- %s -->",
	"<string name='hr_size_sqlunet_db' tools:keep='@string/hr_size_sqlunet_db'>%s</string> <!-- %s -->")

filesizeRes(dbfz,
	"<integer name='size_sqlunet_db_zip' tools:keep='@string/size_sqlunet_db_zip'>%s</integer> <!-- %s -->",
	"<string name='hr_size_sqlunet_db_zip' tools:keep='@string/hr_size_sqlunet_db_zip'>%s</string> <!-- %s -->")

if __name__ == "__main__":

	for s in collectedRaw:
		print(s)
	print()
	for s in collectedHR:
		print(s)
