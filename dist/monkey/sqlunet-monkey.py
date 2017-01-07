# monkey

# imports the monkeyrunner modules used by this program
from com.android.monkeyrunner import MonkeyRunner, MonkeyDevice
from com.android.monkeyrunner.easy import EasyMonkeyDevice, By
import monkeylib

# Apk
apk = '../release/browser-release.apk'

# Package
package = 'org.sqlunet.browser'

# Activities
mainActivity = 'org.sqlunet.browser.MainActivity'
runActivity = 'org.sqlunet.browser.BrowseActivity'

# Bundle
bundle = {'org.sqlunet.BASE': '/data/data/'+package+'/files'}

# Word list
fname='sqlunet_list.txt'

# Monkey
monkey=monkeylib.MonkeyLib(apk,package,mainActivity,runActivity,bundle)
m=monkey

#search for string
ACTION_QUERY = "org.sqlunet.browser.QUERY";

ARG_QUERYSTRING = "QUERYSTRING";
ARG_QUERYPOINTER = "QUERYPOINTER";
ARG_QUERYRECURSE = "QUERYRECURSE";

ARG_QUERYTYPE = "QUERYTYPE";
ARG_QUERYTYPE_ALL = 0;
ARG_QUERYTYPE_WORD = 1;
ARG_QUERYTYPE_SYNSET = 2;
ARG_QUERYTYPE_SENSE = 3;
ARG_QUERYTYPE_VNCLASS = 10;
ARG_QUERYTYPE_PBROLESET = 20;
ARG_QUERYTYPE_FNFRAME = 31;
ARG_QUERYTYPE_FNLEXUNIT = 32;
ARG_QUERYTYPE_FNSENTENCE = 33;
ARG_QUERYTYPE_FNANNOSET = 34;
ARG_QUERYTYPE_FNPATTERN = 35;
ARG_QUERYTYPE_FNVALENCEUNIT = 36;
ARG_QUERYTYPE_FNPREDICATE = 37;
ARG_QUERYTYPE_PM = 40;
ARG_QUERYTYPE_PMROLE = 41;

qs='test'
extras = {ARG_QUERYSTRING: qs, ARG_QUERYTYPE: ARG_QUERYTYPE_WORD, ARG_QUERYRECURSE: False}

# loop
def loopTestNewActivity(m):

	# word lists
	words=monkeylib.getList(fname)

	print('LOOP TEST NEW ACTIVITY')
	for w in words :
		print(w)
		m.activity(w,extras)
		v=m.waitVisible('id/treebolicId')
		m.drag()

def loopTestNewSource(m):

	# word lists
	words=libmonkey.getList(fname)
	words2=reversed(words)

	print('LOOP TEST SEARCHING NEW SOURCE')
	for w in words :

		print(w)
		m.activity(w)
		for w2 in words2 :
			print('	%s' % w2)
			m.search(w2)
			m.clear()
			m.drag()
			m.sleep(3)
		m.back()

def loopSearch():

	# word lists
	words=('force', 'net')
	words2=('#', 'f', 'fo')

	print('LOOP TEST')
	for w in words :

		#print(w)
		m.activity(w,extras)
		for w2 in words2 :
			#print('	%s' % w2)
			m.search(w2)
			for i in range(4):
				m.search_again()
			m.clear()
			m.drag()
			m.sleep(3)
		m.back()

def install():
	m.install()

def mainToBrowse():
	m.mainActivity()
	bi=m.waitVisible('id/content')
	bi=m.waitVisible('id/up')
	m.touchId('id/up')
	bi=m.waitVisible('id/navigation_drawer')
	m.pressKey('KEYCODE_DPAD_DOWN')
	m.pressKey('KEYCODE_DPAD_DOWN')
	m.pressKey('KEYCODE_ENTER')
	m.search('abandon')
	bi=m.waitVisible('id/list')
	m.pressKey('KEYCODE_DPAD_DOWN')
	m.pressKey('KEYCODE_DPAD_DOWN')
	m.pressKey('KEYCODE_ENTER')
	m.touchId('id/node_link')
	while(True):
		m.pressKey('KEYCODE_DPAD_DOWN')
		v=m.waitVisible('id/node_link')
		m.touchId('id/node_link')

def searches():
	m.activity()
	for w in ('inveigle', 'absorb', 'foist', 'foster'):
		m.search(w)

def firstGroup(v):
	#print("NODE",v.class,v.id,v.toString(),v.namedProperties['text:mText'] if v.name == 'android.widget.TextView' else '-')
	if v.id == "id/xselector":
		print("SELECTOR",v.id,v.toString())
	elif v.id == "id/xgroup":
		print("GROUP",v.id,v.toString(),v.children[0].namedProperties['text:mText'])
		#dumpView(v)
		m.touchView(v)
		m.sleep(3)
		return True
	return False

def visitNode(v):
	#print("NODE",v.class,v.id,v.toString(),v.namedProperties['text:mText'] if v.name == 'android.widget.TextView' else '-')
	if v.id == "id/xselector":
		print("SELECTOR",v.id,v.toString())
	elif v.id == "id/xgroup":
		print("GROUP",v.id,v.toString(),v.children[0].namedProperties['text:mText'])
		#dumpView(v)
	return False

def	emptyTraverse(parent): 
	for node in m.traverse(parent):
		print('.')

def	launch(): 
	m.activity()

def	dumpView(vn): 
	print("view name " + vn.name)
	print("view type " + str(type(vn)))
	print("view dir  " + str(dir(vn)))
	print("view class " + str(vn.class))
	print("view children " + str(vn.children))
	print("view window " + str(vn.window))
	print("view window type " + str(type(vn.window)))
	print("view window dir " + str(dir(vn.window)))
	print("view namedProperties ")
	print(dir(vn.namedProperties))
	print(vn.namedProperties)
	print("view text " + str(vn.namedProperties['text:mText']))

def dumpId(bi):
	print(type(bi))
	print(dir(bi))

def dumpIds():
	vs=monkey.getViewIds()
	print('VIEWS ' + str(vs))

#launch()
#m.search('abandon')
bi=m.waitVisible('id/content')

myid='id/container_browse'
myid='id/list'
vn=m.findViewById(myid)
#dumpView(vn)

#m.traverse(vn,printNodeView)
m.ytraverseFrom(vn, firstGroup)
m.sleep(5)
print("now expand")
vn=m.findViewById(myid)
m.rtraverseFrom(vn, visitNode)

#m.sleep(10)
#m.kill()

#------
#fail
#vn=m.findViewById2(myid)

# fail
#v=m.device.getViewById('id/content')
#print(type(v))
#print(dir(v))
#print(v.getChildren())
#print(v.getViewClass())

# fail
#v=m.device.getRootView()
#print(dir(v))


