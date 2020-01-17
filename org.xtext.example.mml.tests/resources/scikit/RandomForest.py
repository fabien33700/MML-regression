import pandas as pd
from sklearn.ensemble import RandomForestRegressor
from sklearn.model_selection import cross_validate
from sklearn.metrics import mean_squared_error
from sklearn.metrics import mean_absolute_error
df = pd.read_csv('Boston.csv', sep=',')
df.head()
y = df["medv"]
X = df.drop(columns=["medv"])
clf = RandomForestRegressor()
scoring = ['neg_mean_absolute_error','neg_mean_squared_error']
results = cross_validate(clf, X, y, cv=6,scoring=scoring)
print('mean_absolute_errors = '+str(results['test_neg_mean_absolute_error']))
print('mean_squared_errors = '+str(results['test_neg_mean_squared_error']))
