packer {
  required_plugins {
    googlecompute = {
      version = ">= 1.1.4"
      source  = "github.com/hashicorp/googlecompute"
    }
  }
}

variable "project_id" {
  description = "The project ID to deploy to"
  type        = string
  default     = "cloud-csye-6225"
}

variable "source_image_family" {
  description = "The source image family to use for the instance"
  type        = string
  default     = "centos-stream-8"
}

variable "zone" {
  description = "The zone to deploy to"
  type        = string
  default     = "us-east1-b"
}

variable "ssh_username" {
  description = "The username to use for SSH access to the instance"
  type        = string
  default     = "centOS"
}

source "googlecompute" "centOS" {
  project_id          = "${var.project_id}"
  source_image_family = "${var.source_image_family}"
  zone                = "${var.zone}"
  ssh_username        = "${var.ssh_username}"
  network             = "default"
  subnetwork          = "default"
}

build {
  sources = ["source.googlecompute.centOS"]

    provisioner "shell" {
      script = "../scripts/update-script.sh"
    }

    provisioner "file" {
      source      = "../target/assignment-0.0.1-SNAPSHOT.jar"
      destination = "/tmp/"
    }

    provisioner "shell" {
      script = "../scripts/java-sql-script.sh"
    }

    provisioner "shell" {
      scripts = ["../scripts/run-jar.sh"]
    }
}