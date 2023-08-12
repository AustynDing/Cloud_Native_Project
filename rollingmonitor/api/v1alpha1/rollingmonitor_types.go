/*
Copyright 2022.
*/

package v1alpha1

import (
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
)

// EDIT THIS FILE!  THIS IS SCAFFOLDING FOR YOU TO OWN!
// NOTE: json tags are required.  Any new fields you add must have json tags for the fields to be serialized.

// RollingMonitorSpec defines the desired state of RollingMonitor
type RollingMonitorSpec struct {
	// INSERT ADDITIONAL SPEC FIELDS - desired state of cluster
	// Important: Run "make" to regenerate code after modifying this file

	DeploymentName string `json:"deploymentName,omitempty"`
}

// RollingMonitorStatus defines the observed state of RollingMonitor
type RollingMonitorStatus struct {
	// INSERT ADDITIONAL STATUS FIELD - define observed state of cluster
	// Important: Run "make" to regenerate code after modifying this file

}

//+kubebuilder:object:root=true
//+kubebuilder:subresource:status

// RollingMonitor is the Schema for the rollingmonitors API
type RollingMonitor struct {
	metav1.TypeMeta   `json:",inline"`
	metav1.ObjectMeta `json:"metadata,omitempty"`

	Spec   RollingMonitorSpec   `json:"spec,omitempty"`
	Status RollingMonitorStatus `json:"status,omitempty"`
}

//+kubebuilder:object:root=true

// RollingMonitorList contains a list of RollingMonitor
type RollingMonitorList struct {
	metav1.TypeMeta `json:",inline"`
	metav1.ListMeta `json:"metadata,omitempty"`
	Items           []RollingMonitor `json:"items"`
}

func init() {
	SchemeBuilder.Register(&RollingMonitor{}, &RollingMonitorList{})
}
