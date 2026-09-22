#!/usr/bin/env bash
# Generates a project from this template into a temp dir and runs its tests.
# The project name is unique per run so sbt 2's build cache cannot replay a
# previous run's result instead of compiling and testing for real.
set -euo pipefail

template_dir="$(cd "$(dirname "$0")/.." && pwd)"
work_dir="$(mktemp -d)"
trap 'rm -rf "$work_dir"' EXIT
project="smoke-$(date +%s)"

cd "$work_dir"
sbt --batch new "file://$template_dir" --name="$project" --organization=com.example
cd "$project"
sbt --server --batch "scalafmtSbtCheck; scalafmtCheckAll; coverage; testFull; coverageReport"
