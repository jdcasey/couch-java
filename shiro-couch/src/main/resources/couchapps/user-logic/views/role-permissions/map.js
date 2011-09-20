function( doc ){
	if ( doc.docType == 'role' ){
		if ( doc.permissions ){
			for( var p in doc.permissions ){
				emit( doc.name, {'_id': 'permission:' + doc.permissions[p]} );
			}
		}
	}
}
