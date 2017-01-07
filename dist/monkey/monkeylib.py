# monkey

# imports the monkeyrunner modules used by this program
from com.android.monkeyrunner import MonkeyRunner, MonkeyDevice
from com.android.monkeyrunner.easy import EasyMonkeyDevice, By

#######################################################################
#	MonkeyLib
#######################################################################

class MonkeyLib:

	def __init__(self,apk,package,mainActivity,runActivity,bundle):
		# Connects to the current device, returning a MonkeyDevice object
		self.device=MonkeyRunner.waitForConnection()
		self.easy_device=EasyMonkeyDevice(self.device)
		self.apk=apk
		self.package=package
		self.bundle=bundle
		self.main=self.component(package,mainActivity)
		self.run=self.component(package,runActivity)

	def component(self,package, activity):
		return package + '/' + activity

	# I N S T A L L / U N I N S T A L L

	def install(self):
		# Installs the Android package. Notice that this method returns a boolean, so you can test to see if the installation worked.
		print('INSTALL APP ' + self.apk)
		self.device.installPackage(self.apk)

	def remove(self):
		print('REMOVE APP ' + self.package)
		self.device.removePackage(self.package)

	# S Y N C

	def waitVisible(self,idstr):
		print('VISIBLE ' + idstr)
		v=By.id(idstr)
		while(not self.easy_device.visible(v)):
			print('wait ' + str(v));
		#print('visible');
		return v

	def sleep(self,s):
		MonkeyRunner.sleep(s)

	# A C T I V I T Y

	def mainActivity(self):
		print('MAIN')
		self.device.startActivity(component=self.main)
		self.sleep(3)

	def activity(self):
		print('ACTIVITY ' + str(self.run))
		self.device.startActivity(component=self.run)
		self.sleep(3)

	def activity2(self,action,extras):
		print('ACTIVITY ' + str(self.run) + ' extras:' + str(extras))
		extras.update(self.bundle)
		self.device.startActivity(component=self.run,action=action,extras=extras)
		self.sleep(3)

	def kill(self):
		print('KILL')
		self.device.shell('am force-stop '+self.package)

	# S E A R C H

	def search(self,w):
		print('SEARCH ' + w)
		v=self.waitVisible('id/search')
		sv=By.id('id/search')
		self.easy_device.touch(sv, MonkeyDevice.DOWN_AND_UP)
		self.easy_device.type(sv, w)
		self.easy_device.press('KEYCODE_ENTER', MonkeyDevice.DOWN_AND_UP)
		self.sleep(3)

	# K E Y B O A R D  E V E N T S

	def pressKey(self,k):
		print('PRESS '+k)
		self.device.press(k, MonkeyDevice.DOWN_AND_UP)

	def back(self):
		print('BACK')
		pressKey('KEYCODE_BACK')

	# T O U C H  E V E N T S

	def touch(self,id):
		print('TOUCH ' + id)
		bid=By.id(id)
		self.easy_device.touch(bid, MonkeyDevice.DOWN_AND_UP)

	def touchView(self,vn):
		print('TOUCH ' + str(vn))
		p = self.device.getHierarchyViewer().getAbsoluteCenterOfView(vn);
		self.device.touch(p.x, p.y, MonkeyDevice.DOWN_AND_UP)

	def touchout(self):
		print('TOUCHOUT')
		self.device.touch(10, 100, MonkeyDevice.DOWN_AND_UP)

	def drag(self):
		print('DRAG')
		self.device.drag((600,400),(300,200),2,10)
		#self.sleep(3)

	# T R A V E R S A L

	def traverseFrom(self,start,process):
		process(start)
		if start.children != None:
			for node in start.children:
				self.traverseFrom(node,process)

	def rtraverseFrom(self,start,process):
		process(start)
		if start.children != None:
			for node in reversed(start.children):
				self.rtraverseFrom(node,process)

	def ytraverse(self,node):
		yield node
		if node.children != None:
			for child in node.children:
				for node2 in self.ytraverse(child):
					yield node2

	def	ytraverseFrom(self,start,process): 
		for node in self.ytraverse(start):
			if process(node):
				break

	def yrtraverse(self,node):
		yield node
		if node.children != None:
			for child in reversed(node.children):
				for node2 in self.yrtraverse(child):
					yield node2

	def	yrtraverseFrom(self,start,process): 
		for node in self.yrtraverse(start):
			if process(node):
				break

	# V I E W S

	def	findViewById(self,id): 
		return self.device.getHierarchyViewer().findViewById(id)

	#def findViewById2(self,id):
	#	By.id(id).findView(self.device.getHierarchyViewer())

	def	getViewIds(self): 
		return self.device.getViewIdList()

	# I M A G I N G

	def snapshot(self):
		# Takes a screenshot: a MonkeyImage
		return self.device.takeSnapshot()

	def toFile(self,shot, fname):
		# Writes the screenshot to a file
		shot.writeToFile('shot-'+ fname +'.png','png')

def getList(fname):
	# word lists
	f=open(fname)
	words=f.readlines()
	words=[w.strip(' \t\n\r') for w in words]
	f.close()
	return words

