function(doc){
	if( doc.doctype == 'proxy' ){
		emit(doc.name,{'_id': doc._id});
	}
}