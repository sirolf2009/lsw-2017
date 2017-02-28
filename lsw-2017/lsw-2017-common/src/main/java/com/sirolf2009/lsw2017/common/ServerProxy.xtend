package com.sirolf2009.lsw2017.common

import com.sirolf2009.lsw2017.common.model.PointRequest

interface ServerProxy {
	
	def void requestPoints(PointRequest request)
	
}