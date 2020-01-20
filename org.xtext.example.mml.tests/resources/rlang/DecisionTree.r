library(caret)
library(e1071)
library(rpart)
library(rpart.plot)
library(dplyr)
set.seed(678)
path <- 'titanic.csv'
titanic <- read.csv(path)
shuffle_index <- sample(1:nrow(titanic))
titanic <- titanic[shuffle_index, ]
clean_titanic <- titanic
mutate(pclass = factor(pclass, levels = c(1, 2, 3), labels = c('Upper', 'Middle', 'Lower')),
survived = factor(survived, levels = c(0, 1), labels = c('No', 'Yes'))) % > %
na.omit()
create_train_test <- function(data, size = 0.7, train = TRUE) {
n_row = nrow(data)
total_row = size * n_row
train_sample <- 1: total_row
if (train == TRUE) {
return (data[train_sample, ])
} else {
return (data[-train_sample, ])
}
}
data_train <- create_train_test(titanic, 0.7, train = TRUE)
data_test <- create_train_test(titanic, 0.7, train = FALSE)
fit <- rpart(survived~., data = data_train, method = 'class')
predict_unseen <-predict(fit, data_test, type = 'class')
table_mat <- table(data_test$survived, predict_unseen)

accuracy_Test <- sum(diag(table_mat)) / sum(table_mat)
print(paste('Accuracy for test', accuracy_Test))

