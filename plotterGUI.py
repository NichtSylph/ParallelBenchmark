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
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt

# Load the JSON data from the file
file_path = 'CSC375\\ClientEmulator-JS\\clientemulator\\src\\main\\java\\clientemu\\benchmark_results.json'
with open(file_path, 'r') as file:
    benchmark_data = json.load(file)

# Define operation type dictionary for reference
operation_type_dict = {'0': 'Add', '1': 'Get', '2': 'Update', '3': 'Remove'}

# Organize the data into a list for DataFrame creation
operation_scores_list = []
for entry in benchmark_data:
    operation_type = operation_type_dict[entry['params']['operationType']]
    concurrency_type = 'Custom' if 'Custom' in entry['benchmark'] else 'Standard'
    score = entry['primaryMetric']['score']
    operation_scores_list.append({
        'Operation Type': operation_type,
        'Concurrency Type': concurrency_type,
        'Score': score
    })

# Create a DataFrame from the list
df_scores = pd.DataFrame(operation_scores_list)

# Define a function to plot the operation scores with a parabola for a given operation type
def plot_operation_with_parabola(operation, df_scores):
    # Extract the scores for the given operation
    custom_scores = df_scores[(df_scores['Operation Type'] == operation) & (df_scores['Concurrency Type'] == 'Custom')]['Score']
    standard_scores = df_scores[(df_scores['Operation Type'] == operation) & (df_scores['Concurrency Type'] == 'Standard')]['Score']
    
    # Prepare data for the plot
    x_custom = np.ones(len(custom_scores))
    x_standard = 2 * np.ones(len(standard_scores))
    x_values = np.concatenate([x_custom, x_standard])
    y_values = np.concatenate([custom_scores, standard_scores])
    
    # Fit a quadratic function to the combined data
    coefs = np.polyfit(x_values, y_values, 2)
    parabola = np.poly1d(coefs)
    
    # Generate x values for the parabola plot
    x_fit = np.linspace(0.5, 2.5, 200)
    y_fit = parabola(x_fit)
    
    # Create a new figure for the plot
    plt.figure(figsize=(8, 6))
    
    # Create scatter plots
    plt.scatter(x_custom, custom_scores, color='blue', alpha=0.7, label='Custom Concurrency')
    plt.scatter(x_standard, standard_scores, color='red', alpha=0.7, label='Standard Concurrency')
    
    # Plot the parabola
    plt.plot(x_fit, y_fit, color='green', linestyle='--', label='Fitted Parabola')
    
    # Formatting the plot
    plt.xticks([1, 2], ['Custom', 'Standard'])
    plt.title(f'{operation} Operation')
    plt.ylabel('Score (ops/s)')
    plt.legend()
    
    # Show the plot
    plt.show()

# Now call the plotting function for each operation type
for operation in ['Add', 'Get', 'Update', 'Remove']:
    plot_operation_with_parabola(operation, df_scores)
