function( doc ){
	if ( doc.doctype == 'role' ){
		if ( doc.permissions ){
			for( var p in doc.permissions ){
				emit( doc.permissions[p], {'_id': doc._id} );
			}
		}
	}
}
