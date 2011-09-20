function( doc ){
  if ( doc.docType == 'user' ){
	if ( doc.roles ){
	  for( var r in doc.roles ){
		emit( doc.username, {'_id': 'role:' + doc.roles[r]} );
	  }
	}
  }
}
