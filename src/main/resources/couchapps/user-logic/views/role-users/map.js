function( doc ){
  if ( doc.doctype == 'user' ){
	if ( doc.roles ){
	  for( var r in doc.roles ){
		emit( doc.roles[r], {'_id': doc._id} );
	  }
	}
  }
}
