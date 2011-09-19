function( doc ){
  if ( doc.doctype == 'user' ){
	if ( doc.roles ){
	  for( var r in doc.roles ){
		emit( doc.username, {'_id': 'role:' + doc.roles[r]} );
	  }
	}
  }
}
