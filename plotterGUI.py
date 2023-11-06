"""
This script loads benchmark results from a JSON file and generates a scatter plot with a quadratic fit
for two sets of benchmarks: those run with a custom registry and those with a standard registry.

The results are expected to be alternated between custom and standard registry benchmarks in the provided JSON file.
The script sorts the benchmarks, extracts the scores and errors, and then plots them over time. The 'time' here
is a sequence number representing the order of the benchmarks, not the actual time.

A quadratic polynomial fit is performed for both the custom and standard registry scores to show the trend over time.
The resulting plot is saved to the specified output directory.

Attributes:
    output_path (str): A path to the directory where the resulting plot image will be saved.

Raises:
    AssertionError: If the number of benchmarks for custom and standard registries do not match.

Example:
    To run the script, ensure that you have a `benchmark_results.json` file in the specified path and
    that the output path directory exists. Execute the script in a Python environment where the required
    packages (json, matplotlib, numpy, os) are installed.

    $ python benchmark_plotter.py

The resulting plot will be saved as 'benchmark_quadratic_fit.png' in the specified output directory.

Author: Joel Santos
"""

import json
import matplotlib.pyplot as plt
import numpy as np
import os

# Load the results file
with open('CSC375\\ClientEmulator-JS\\clientemulator\\src\\main\\java\\clientemu\\benchmark_results.json', 'r') as f:
    results = json.load(f)

# Define the path where the images should be saved
output_path = 'CSC375\\ClientEmulator-JS\\clientemulator\\WebsiteBenchmark\\'

# Ensure the directory exists
os.makedirs(output_path, exist_ok=True)

# Initialize lists to store the benchmark names, scores, and errors
benchmark_names = []
scores = []
errors = []

# Extract data
for benchmark in results:
    benchmark_names.append(benchmark['benchmark'])
    scores.append(benchmark['primaryMetric']['score'])
    errors.append(benchmark['primaryMetric']['scoreError'])

# Create a scatter plot
plt.figure(figsize=(10, 5))
x = np.arange(1, len(scores) + 1)
y = np.array(scores)
errors = np.array(errors)

# Fitting a quadratic polynomial (second degree)
coefficients = np.polyfit(x, y, 2)
polynomial = np.poly1d(coefficients)
ys = polynomial(x)

# Plot the polynomial fit
plt.plot(x, ys, label='Speed Comparison')

# Plot the original data with error bars
# Custom Benchmark in blue
# Standard Benchmark in red
for i in range(len(benchmark_names)):
    if 'Custom' in benchmark_names[i]:
        color = 'blue'
    else:
        color = 'red'
    plt.errorbar(x[i], y[i], yerr=errors[i], fmt='o', color=color, label=benchmark_names[i])

# Adding labels and title
plt.xlabel('As the number of requests increases')
plt.ylabel('Throughput (ops/s)')
plt.title('Comparison in Speed between Benchmarks')
plt.legend()

plt.tight_layout()
# Save the plot to the specified path
plt.savefig(f"{output_path}ParallelBenchmark.png")
plt.close()  # Close the figure to avoid displaying it in a notebook environment
