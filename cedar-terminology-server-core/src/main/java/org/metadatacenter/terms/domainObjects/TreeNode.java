package org.metadatacenter.terms.domainObjects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.wordnik.swagger.annotations.ApiModel;

import java.util.List;

@ApiModel
@JsonPropertyOrder({"id", "@id", "@type", "label", "hasChildren", "children", "obsolete"})
public class TreeNode {
  private String id;
  @JsonProperty("@id")
  private String ldId;
  @JsonProperty("@type")
  private String type;
  private String label;
  private boolean hasChildren;
  private List<TreeNode> children;
  private boolean obsolete;

  // The default constructor is used by Jackson for deserialization
  public TreeNode() {
  }

  public TreeNode(String id, String ldId, String type, String label, boolean hasChildren, List<TreeNode> children,
                  boolean obsolete) {
    this.id = id;
    this.ldId = ldId;
    this.type = type;
    this.label = label;
    this.hasChildren = hasChildren;
    this.children = children;
    this.obsolete = obsolete;
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

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public boolean getHasChildren() {
    return hasChildren;
  }

  public void setHasChildren(boolean hasChildren) {
    this.hasChildren = hasChildren;
  }

  public List<TreeNode> getChildren() {
    return children;
  }

  public void setChildren(List<TreeNode> children) {
    this.children = children;
  }

  public boolean isObsolete() {
    return obsolete;
  }

  public void setObsolete(boolean obsolete) {
    this.obsolete = obsolete;
  }
}
