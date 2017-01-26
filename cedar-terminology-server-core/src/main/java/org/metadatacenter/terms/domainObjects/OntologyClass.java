package org.metadatacenter.terms.domainObjects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
//import com.wordnik.swagger.annotations.ApiModel;
//import com.wordnik.swagger.annotations.ApiModelProperty;

import static org.metadatacenter.terms.util.Constants.BP_TYPE_BASE;
import static org.metadatacenter.terms.util.Constants.BP_TYPE_CLASS;

import java.util.List;

//@ApiModel
@JsonPropertyOrder({"id", "@id", "@type", "type", "prefLabel", "creator", "ontology", "definitions", "synonyms", "subclassOf",
    "relations", "provisional", "created", "hasChildren"})
public class OntologyClass {
  //@ApiModelProperty(hidden = true)
  private String id;
  //@ApiModelProperty(hidden = true)
  @JsonProperty("@id")
  private String ldId;
  @JsonProperty("@type")
  private String ldType = BP_TYPE_BASE + BP_TYPE_CLASS;
  private String type = BP_TYPE_CLASS;
  //@ApiModelProperty(required = true)
  private String prefLabel;
  //@ApiModelProperty(required = true)
  private String creator;
  //@ApiModelProperty(required = true)
  private String ontology;
  //@ApiModelProperty(required = false)
  private List<String> definitions;
  //@ApiModelProperty(required = false)
  private List<String> synonyms;
  //@ApiModelProperty(required = false)
  private String subclassOf;
  //@ApiModelProperty(required = false)
  private List<Relation> relations;
  //@ApiModelProperty(hidden = true)
  private boolean provisional;
  //@ApiModelProperty(hidden = true)
  private String created;
  //@ApiModelProperty(required = false)
  private Boolean hasChildren;

  // The default constructor is used by Jackson for deserialization
  public OntologyClass() {
  }

  public OntologyClass(String id, String ldId, String prefLabel, String creator, String ontology, List<String>
      definitions, List<String> synonyms, String subclassOf, List<Relation> relations, boolean provisional, String
                           created, Boolean hasChildren) {
    this.id = id;
    this.ldId = ldId;
    this.prefLabel = prefLabel;
    this.creator = creator;
    this.ontology = ontology;
    this.definitions = definitions;
    this.synonyms = synonyms;
    this.subclassOf = subclassOf;
    this.relations = relations;
    this.provisional = provisional;
    this.created = created;
    this.hasChildren = hasChildren;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getLdId() {
    return ldId;
  }

  public void setLdId(String ldId) {
    this.ldId = ldId;
  }

  public String getLdType() {
    return ldType;
  }

  public void setLdType(String ldType) {
    this.ldType = ldType;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getPrefLabel() {
    return prefLabel;
  }

  public void setPrefLabel(String prefLabel) {
    this.prefLabel = prefLabel;
  }

  public String getCreator() {
    return creator;
  }

  public void setCreator(String creator) {
    this.creator = creator;
  }

  public String getOntology() {
    return ontology;
  }

  public void setOntology(String ontology) {
    this.ontology = ontology;
  }

  public List<String> getDefinitions() {
    return definitions;
  }

  public void setDefinitions(List<String> definitions) {
    this.definitions = definitions;
  }

  public List<String> getSynonyms() {
    return synonyms;
  }

  public void setSynonyms(List<String> synonyms) {
    this.synonyms = synonyms;
  }

  public String getSubclassOf() {
    return subclassOf;
  }

  public void setSubclassOf(String subclassOf) {
    this.subclassOf = subclassOf;
  }

  public List<Relation> getRelations() {
    return relations;
  }

  public void setRelations(List<Relation> relations) {
    this.relations = relations;
  }

  public boolean isProvisional() {
    return provisional;
  }

  public void setProvisional(boolean provisional) {
    this.provisional = provisional;
  }

  public String getCreated() {
    return created;
  }

  public void setCreated(String created) {
    this.created = created;
  }

  public Boolean getHasChildren() {
    return hasChildren;
  }

  public void setHasChildren(Boolean hasChildren) {
    this.hasChildren = hasChildren;
  }
}
