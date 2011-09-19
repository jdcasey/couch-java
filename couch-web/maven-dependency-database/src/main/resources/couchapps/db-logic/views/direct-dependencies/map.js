function( doc ) {
  emit(
    doc.dependent.groupId + ":" + doc.dependent.artifactId + ":" + doc.dependent.version,
    doc.dependency.groupId + ":" + doc.dependency.artifactId + ":" + doc.dependency.version
  );
}
