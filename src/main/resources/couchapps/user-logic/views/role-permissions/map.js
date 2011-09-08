function( doc ){
	if ( doc.doctype == 'role' ){
		if ( doc.permissions ){
			for( var p in doc.permissions ){
				emit( doc.name, {'_id': 'permission:' + doc.permissions[p]} );
			}
		}
	}
}
