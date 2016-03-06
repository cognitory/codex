#!/bin/bash

set -euo pipefail

main() {
  curl "https://api.github.com/repos/cognitory/codex/contents/guides" > resources/guides.json
  curl "https://api.github.com/repos/cognitory/codex/contents/tldrs" > resources/tldrs.json
}

main
