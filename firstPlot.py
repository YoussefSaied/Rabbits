import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import matplotlib
from sklearn.linear_model import LinearRegression


data_dict = {}
data_dict2 = {}

directory = 'experimentsData'

for i in range(10, 26):
	data_dict['data_{}'.format(i)] = pd.read_csv('{}/totalGrass{}.csv'.format(directory, i))


for i in range(30, 110, 10):
	data_dict['data_{}'.format(i)] = pd.read_csv('{}/totalGrass{}.csv'.format(directory, i))
	data_dict2['data_{}'.format(i)] = pd.read_csv('{}/totalGrass{}.csv'.format(directory, i))

for i in range(150, 450, 50):
	data_dict['data_{}'.format(i)] = pd.read_csv('{}/totalGrass{}.csv'.format(directory, i))
	data_dict2['data_{}'.format(i)] = pd.read_csv('{}/totalGrass{}.csv'.format(directory, i))

to_ploty_grass = []
to_ploty_rabbits = []
to_ploty_rabbits2 = []
# to_ploty_energy = []
to_plotx = [i for i in range(10, 26)] +  [i for i in range(30, 110, 10)] + [i for i in range(150, 450, 50)]

for key in data_dict.keys():
	to_ploty_grass.append(data_dict[key].iloc[-100:, 1].sum()/100)
	to_ploty_rabbits.append(data_dict[key].iloc[-100:, 2].sum()/100)
	# to_ploty_energy.append(data_dict[key].iloc[-100:, 3].sum()/100)

for key in data_dict2.keys():
	to_ploty_rabbits2.append(data_dict2[key].iloc[-100:, 2].sum()/100)

X = np.array([i for i in range(30, 110, 10)] + [i for i in range(150, 450, 50)]).reshape(-1, 1)
X = np.concatenate((X, np.ones((X.shape[0], 1))), axis=1)
y = np.array(to_ploty_rabbits2).reshape(-1, 1)

reg = LinearRegression().fit(X, y)

coef1 = reg.coef_[0][0]
coef2 = reg.coef_[0][1]

print('coef1 : {}'.format(coef1))
print('coef2 : {}'.format(coef2))

pred = reg.predict(X)

plt.rcParams["figure.figsize"] = (8, 5)

fig, ax1 = plt.subplots()

color = 'tab:red'
ax1.set_xlabel('Grass Growth Rate', fontsize=15)
ax1.set_ylabel('Avg. Amount of Grass', color=color, fontsize=15)
ax1.plot(to_plotx, to_ploty_grass, color=color)
ax1.tick_params(axis='y', labelcolor=color)

plt.xticks(fontsize=15)
plt.yticks(fontsize=15)

ax2 = ax1.twinx()  # instantiate a second axes that shares the same x-axis

color = 'tab:green'
# ax2.set_ylabel('Predicted Number of Rabbits', color=color)  # we already handled the x-label with ax1
ax2.plot([i for i in range(30, 110, 10)] + [i for i in range(150, 450, 50)], pred, color=color, linestyle='dashed', label='Line of best fit')
ax2.tick_params(axis='y', labelcolor=color)

color = 'tab:blue'
ax2.set_ylabel('Avg. Number of Rabbits', color=color, fontsize=15)  # we already handled the x-label with ax1
ax2.plot(to_plotx, to_ploty_rabbits, color=color)
ax2.tick_params(axis='y', labelcolor=color)

fig.tight_layout()  # otherwise the right y-label is slightly clipped
plt.title('Grass Growth Rate - Average Number of Rabbits and Grass', fontsize=18)
plt.legend(fontsize=15, loc='lower right')
plt.tight_layout()
plt.xticks(fontsize=15)
plt.yticks(fontsize=15)
plt.show()
