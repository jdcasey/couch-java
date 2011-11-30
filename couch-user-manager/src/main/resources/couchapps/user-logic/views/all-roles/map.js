function(doc){
	if( doc.doctype == 'role' ){
		emit(doc.name,{'_id': doc._id});
	}
}
