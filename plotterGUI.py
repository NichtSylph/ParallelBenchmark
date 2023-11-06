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

# Define the path where the benchmark results JSON file is located
json_file_path = 'CSC375\\ClientEmulator-JS\\clientemulator\\src\\main\\java\\clientemu\\benchmark_results.json'

# Define the path where the images should be saved
output_path = 'CSC375\\ClientEmulator-JS\\clientemulator\\WebsiteBenchmark'

def load_benchmark_results(file_path):
    """
    Load the benchmark results from a JSON file.
    
    Args:
        file_path (str): The path to the JSON file containing the benchmark results.
    
    Returns:
        list: A list of dictionaries containing the benchmark results.
    """
    with open(file_path, 'r') as f:
        return json.load(f)

def plot_benchmark_results(results, output_directory):
    """
    Plot the benchmark results and save the plot to a file.
    
    Args:
        results (list): A list of dictionaries containing the benchmark results.
        output_directory (str): The directory where the plot image will be saved.
    """
    # Initialize lists to store the benchmark scores and errors for both registries
    custom_registry_scores = []
    standard_registry_scores = []
    custom_registry_errors = []
    standard_registry_errors = []
    
    # Sort the benchmarks by name to align the custom and standard registry benchmarks
    results.sort(key=lambda x: x['benchmark'])
    
    # Collect scores and errors for each type of registry
    for benchmark in results:
        score = benchmark['primaryMetric']['score']
        error = benchmark['primaryMetric']['scoreError']
        if 'customRegistry' in benchmark['benchmark']:
            custom_registry_scores.append(score)
            custom_registry_errors.append(error)
        else:
            standard_registry_scores.append(score)
            standard_registry_errors.append(error)

    # Plot the benchmarks
    plt.figure(figsize=(10, 5))

    # Plot only up to the minimum length of the two sets
    min_length = min(len(custom_registry_scores), len(standard_registry_scores))
    time_passed = np.arange(1, min_length + 1)
    
    # Error bars and scatter plot for custom registry scores
    plt.errorbar(time_passed, custom_registry_scores[:min_length], yerr=custom_registry_errors[:min_length], fmt='o', color='red', label='Custom Registry')
    
    # Error bars and scatter plot for standard registry scores
    plt.errorbar(time_passed, standard_registry_scores[:min_length], yerr=standard_registry_errors[:min_length], fmt='o', color='blue', label='Standard Registry')
    
    # Perform and plot quadratic fit for custom registry scores
    if len(custom_registry_scores) > 2:
        coeff_custom = np.polyfit(time_passed, custom_registry_scores[:min_length], 2)
        polynomial_custom = np.poly1d(coeff_custom)
        ys_custom = polynomial_custom(time_passed)
        plt.plot(time_passed, ys_custom, color='red', alpha=0.5, label='Custom Registry Fit')
    
    # Perform and plot quadratic fit for standard registry scores
    if len(standard_registry_scores) > 2:
        coeff_standard = np.polyfit(time_passed, standard_registry_scores[:min_length], 2)
        polynomial_standard = np.poly1d(coeff_standard)
        ys_standard = polynomial_standard(time_passed)
        plt.plot(time_passed, ys_standard, color='blue', alpha=0.5, label='Standard Registry Fit')
    
    # Add labels, title, and legend
    plt.xlabel('Time Passed (arbitrary units)')
    plt.ylabel('Throughput (ops/s)')
    plt.title('Benchmark Scores Over Time')
    plt.legend()

    # Adjust layout to prevent overlap
    plt.tight_layout()
    
    # Check if the output directory exists, if not create it
    if not os.path.exists(output_directory):
        os.makedirs(output_directory)
    
    # Save the figure to the output directory
    plt.savefig(os.path.join(output_directory, 'benchmark_quadratic_fit.png'))
    
    # Close the figure
    plt.close()

# Load the benchmark results
benchmark_results = load_benchmark_results(json_file_path)

# Plot the benchmark results and save the plot
plot_benchmark_results(benchmark_results, output_path)
