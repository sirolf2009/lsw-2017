package com.sirolf2009.lsw2017.server.net

import com.sirolf2009.lsw2017.common.ServerProxy
import com.esotericsoftware.kryonet.Connection
import com.esotericsoftware.kryonet.rmi.ObjectSpace
import rx.subjects.PublishSubject
import com.sirolf2009.lsw2017.common.model.PointRequest
import org.eclipse.xtend.lib.annotations.Accessors

class Facade extends Connection implements ServerProxy {
	
	@Accessors val PublishSubject<PointRequest> subject
	
	new() {
		new ObjectSpace(this).register(1, this)
		subject = PublishSubject.create()
	}
	
	override requestPoints(PointRequest request) {
		subject.onNext(request)
	}
	
	
}