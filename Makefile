###
##. Configuration
###

#. This installs/updates the included makefiles
MAKEFILES_REPOSITORY:=https://github.com/sverholen/makefiles.git
MAKEFILES_DIRECTORY:=.makefiles
MAKEFILES_TAG:=v0.0.0
MAKEFILES_LOG:=$(shell \
	if test ! -d $(MAKEFILES_DIRECTORY); then git clone $(MAKEFILES_REPOSITORY) "$(MAKEFILES_DIRECTORY)"; fi; \
	cd "$(MAKEFILES_DIRECTORY)"; \
	if [ -n "$(MAKEFILES_TAG)" ] && [ "$(MAKEFILES_TAG)" != "v0.0.0" ] && \
	[ -z "$$(git --no-pager describe --always --dirty | grep "^$(MAKEFILES_TAG)")" ]; then \
	git fetch --all --tags; git reset --hard "tags/$(MAKEFILES_TAG)"; \
	else git pull; fi \
)

#. This section contains the variables required by the included makefiles, before including the makefiles themselves.
#. In this case, these variables define the own directory as repository to update with the commands in git.makefile
REPOSITORIES=self VSDS-LDESServer4J
REPOSITORY_DIRECTORY_VSDS-LDESServer4J=.
# REPOSITORY_URL_VSDS-LDESServer4J=git@github.com:Informatievlaanderen/VSDS-LDESServer4J.git
REPOSITORY_URL_VSDS-LDESServer4J=git@github.com:sverholen/VSDS-LDESServer4J.git

#. At least include the includes/base.makefile and includes/git.makefile files
include $(MAKEFILES_DIRECTORY)/builtins.makefile  # Reset the default makefile builtins
include $(MAKEFILES_DIRECTORY)/base.makefile      # Base functionality
include $(MAKEFILES_DIRECTORY)/git.makefile       # Git management
include $(MAKEFILES_DIRECTORY)/maven.makefile     # Maven management
# include $(MAKEFILES_DIRECTORY)/docker-compose-services.makefile

###
## VSDS-LDESServer4J
###

.PHONY: help

# Create the .env file
.env: .env.dist
	@if test ! -f "$(@)"; then cp "$(<)" "$(@)"; fi
	@touch "$(@)"

# Clone all repositories
clone: clone-repositories
	@true

# Pull all repositories
pull: pull-repositories
	@true

# Build the images
docker-build: build-images -f docker-compose.yml
	@true
