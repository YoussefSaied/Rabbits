import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import matplotlib
from sklearn.linear_model import LinearRegression


directory = 'experimentsData'

data_dict = {}
data_dict2 = {}

for i in range(10, 26):
	data_dict['data_{}'.format(i)] = pd.read_csv('{}/totalGrass{}.csv'.format(directory, i))


for i in range(30, 110, 10):
	data_dict['data_{}'.format(i)] = pd.read_csv('{}/totalGrass{}.csv'.format(directory, i))
	data_dict2['data_{}'.format(i)] = pd.read_csv('{}/totalGrass{}.csv'.format(directory, i))

for i in range(150, 1050, 50):
	data_dict['data_{}'.format(i)] = pd.read_csv('{}/totalGrass{}.csv'.format(directory, i))
	# data_dict2['data_{}'.format(i)] = pd.read_csv('{}/totalGrass{}.csv'.format(directory, i))

to_ploty_grass = []
to_ploty_rabbits = []
to_ploty_rabbits2 = []
# to_ploty_energy = []
to_plotx = [i for i in range(10, 26)] +  [i for i in range(30, 110, 10)]

for key in data_dict.keys():
	to_ploty_grass.append(data_dict[key].iloc[-100:, 1].sum()/100)
	to_ploty_rabbits.append(data_dict[key].iloc[-100:, 2].sum()/100)
	# to_ploty_energy.append(data_dict[key].iloc[-100:, 3].sum()/100)


for key in data_dict2.keys():
	to_ploty_rabbits2.append(data_dict2[key].iloc[-100:, 2].sum()/100)

# X = np.array([i for i in range(30, 110, 10)]).reshape(-1, 1)
# X = np.concatenate((X, np.ones((X.shape[0], 1))), axis=1)
# y = np.array(to_ploty_rabbits2).reshape(-1, 1)

# reg = LinearRegression().fit(X, y)

# coef1 = reg.coef_[0][0]
# coef2 = reg.coef_[0][1]

# pred = reg.predict(X)

plt.rcParams["figure.figsize"] = (8, 5)

fig, ax1 = plt.subplots()

key = 'data_400'

color = 'tab:red'
ax1.set_xlabel('Time steps', fontsize=15)
ax1.set_ylabel('Total Amount of Grass', color=color, fontsize=15)
legend1 = ax1.plot(data_dict[key].index, data_dict[key].iloc[:, 1], color=color, label='Total Amount of Grass')
ax1.tick_params(axis='y', labelcolor=color)
plt.xticks(fontsize=15)
plt.yticks(fontsize=15)

# ax1.legend(legend1)
# plt.legend(fontsize=15, loc=0)

ax2 = ax1.twinx()  # instantiate a second axes that shares the same x-axis


# color = 'tab:green'
# # ax2.set_ylabel('Predicted Number of Rabbits', color=color)  # we already handled the x-label with ax1
# ax2.plot(data_dict['data_50'].index, data_dict['data_50'].iloc[:, 2], color=color, linestyle='dashed', label='Total Number of Rabbits')
# ax2.tick_params(axis='y', labelcolor=color)

color = 'tab:blue'
ax2.set_ylabel('Total Number of Rabbits', color=color, fontsize=15)  # we already handled the x-label with ax1
legend2 = ax2.plot(data_dict[key].index, data_dict[key].iloc[:, 2], color=color, label='Total Number of Rabbits')
ax2.tick_params(axis='y', labelcolor=color)

fig.tight_layout()  # otherwise the right y-label is slightly clipped
plt.title('Grass Growth Rate {} - Total Number of Rabbits and Grass'.format(key[-3:]), fontsize=18)
# plt.legend(fontsize=15, loc=3)
# ax2.legend(legend2)
plt.tight_layout()
plt.xticks(fontsize=15)
plt.yticks(fontsize=15)
plt.show()
