function(doc){
	if( doc.doctype == 'user' ){
		emit(doc.name,{'_id': doc._id});
	}
}
