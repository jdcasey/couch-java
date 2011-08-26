function(doc) {
  emit(
    doc.dependency.groupId + ":" + doc.dependency.artifactId + ":" + doc.dependency.version,
    doc.dependent.groupId + ":" + doc.dependent.artifactId + ":" + doc.dependent.version
  );
}