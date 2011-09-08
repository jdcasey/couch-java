function(doc){
	if( doc.doctype == 'permission' ){
		emit(doc.name,{'_id': doc._id});
	}
}