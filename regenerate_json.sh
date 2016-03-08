#!/bin/bash

set -euo pipefail

main() {
  curl "https://api.github.com/repos/cognitory/codex/contents/guides" > resources/public/guides.json
  curl "https://api.github.com/repos/cognitory/codex/contents/tldrs" > resources/public/tldrs.json
}

main
